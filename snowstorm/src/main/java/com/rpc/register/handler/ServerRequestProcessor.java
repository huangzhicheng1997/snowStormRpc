package com.rpc.register.handler;

import com.rpc.register.common.MessageType;
import com.rpc.register.common.RequestCode;
import com.rpc.register.config.RouterConfig;
import com.rpc.register.core.ChannelHolderForRouter;
import com.rpc.register.core.DispatcherRouterInfo;
import com.rpc.register.protocol.MessageProtocol;
import com.rpc.register.protocol.body.*;
import com.rpc.register.protocol.header.BaseMessage;
import com.rpc.register.remote.RemotingClient;
import com.rpc.register.remote.RemotingServer;
import com.rpc.register.util.IpUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author: hzc
 * @Date: 2020/03/19  09:36
 * @Description:
 */
public class ServerRequestProcessor implements RequestProcessor {
    private RemotingServer remotingServer;
    private RemotingClient remotingClient;

    public ServerRequestProcessor(RemotingServer remotingServer, RemotingClient remotingClient) {
        this.remotingServer = remotingServer;
        this.remotingClient = remotingClient;
    }

    @Override
    public BaseMessage requestHandle(ChannelHandlerContext ctx, MessageProtocol messageProtocol) {
        System.out.println("接受消息 request ：" + ctx.channel().remoteAddress() + "Msg" + messageProtocol);
        BaseMessage content = messageProtocol.getContent();
        try {
            switch (content.getmCode()) {
                case RequestCode.RPC_MSG:
                    return handleRPCMSG(ctx, messageProtocol.getContent());
                case RequestCode.CALLER_AUTH:
                    return handlerCallerHand(ctx, messageProtocol.getContent());
                case RequestCode.FIND_NEXT_ROUTER:
                    return handleFindRouter(ctx, messageProtocol.getContent());
                case RequestCode.REGISTE_ROUTERS:
                    return handleRouterRegisterEvent(ctx, messageProtocol.getContent());
                case RequestCode.LOAD_BALANCE:
                    return handleLoadBalance(ctx, messageProtocol.getContent());
                default:
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * dispatcher处理负载均衡
     *
     * @param ctx
     * @param content
     * @return
     */
    private BaseMessage handleLoadBalance(ChannelHandlerContext ctx, BaseMessage content) {
        BalanceReq balanceReq = (BalanceReq) content;
        String randomNode = DispatcherRouterInfo.getInstance().getRandomNode();
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setMessageId(balanceReq.getMessageId());
        balanceResponse.setRouterAddr(randomNode);
        ctx.channel().writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, balanceResponse));
        return balanceResponse;
    }

    /**
     * dispatcher处理router握手消息
     *
     * @param ctx
     * @param content
     * @return
     */
    private BaseMessage handleRouterRegisterEvent(ChannelHandlerContext ctx, BaseMessage content) {
        RouterHandMsgReq routerHandMsgReq = (RouterHandMsgReq) content;
        String appSets = routerHandMsgReq.getAppSets();
        Set<String> set = null;
        if (StringUtils.isBlank(appSets)) {
            set = new HashSet<>();
        } else {
            set = Arrays.stream(appSets.split(",")).collect(Collectors.toSet());
        }
        DispatcherRouterInfo.getInstance().put(routerHandMsgReq.getAddr(), set);

        RouterHandMsgResponse routerHandMsgResponse = new RouterHandMsgResponse();
        routerHandMsgResponse.setMessageId(routerHandMsgReq.getMessageId());
        ctx.channel().writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, routerHandMsgResponse));
        return content;
    }

    /**
     * dispatcher处理路由寻址
     *
     * @param ctx
     * @param content
     * @return
     */
    private BaseMessage handleFindRouter(ChannelHandlerContext ctx, BaseMessage content) {
        SelectNextRouterReq nextRouterReq = (SelectNextRouterReq) content;
        String targetRouterAddr = DispatcherRouterInfo.getInstance().findTargetRouterFromRouterRequest(nextRouterReq);
        if (StringUtils.isBlank(targetRouterAddr)) {
            System.out.println("not find target service:" + nextRouterReq.getTargetApp());
        }
        //todo null的targetRouter换成枚举
        SelectNextRouterResponse selectNextRouterResponse = new SelectNextRouterResponse();
        selectNextRouterResponse.setNextRouterIp(targetRouterAddr);
        selectNextRouterResponse.setMessageId(content.getMessageId());
        ctx.channel().writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, selectNextRouterResponse));

        return content;
    }

    /**
     * Router处理rpc消息
     *
     * @param ctx
     * @param baseMessage
     * @return
     * @throws InterruptedException
     */
    private BaseMessage handleRPCMSG(ChannelHandlerContext ctx, BaseMessage baseMessage) throws InterruptedException {
        assert baseMessage instanceof RpcReq;
        RpcReq rpcReq = (RpcReq) baseMessage;

        //2为终点路由
        if (rpcReq.getRouteLengthLong() == 2) {
            endRouterHandle(rpcReq, ctx);
        } else {
            Channel channel = ChannelHolderForRouter.getInstance().findChannel(rpcReq);
            if (null != channel) {
                endRouterHandle(rpcReq, ctx);
                return baseMessage;
            }
            //增加一个步长
            rpcReq.addAndGetRouteLength();
            //记录原id
            String oldMsgId = baseMessage.getMessageId();
            //设置新id
            baseMessage.setMessageId(UUID.randomUUID().toString());
            //注册中心获取目标路由的位置
            BaseMessage result = null;
            String nextRouterAddr = nextRouterAddr(ctx, baseMessage);
            if (StringUtils.isNotBlank(nextRouterAddr)) {
                //远程发送消息并同步等待应答
                result = remotingClient.sendRequest(nextRouterAddr, baseMessage);
                if (null == result) {
                    ctx.channel().writeAndFlush(MessageProtocol.errorMsg("get Rpc msg timeout",oldMsgId));
                    return null;
                }
            } else {
                RpcResponse rpcResponse = new RpcResponse();
                rpcResponse.setRpcResult(0);
                result = rpcResponse;

            }
            result.setMessageId(oldMsgId);
            ctx.channel().writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, result));
        }
        return null;
    }

    /**
     * 处理终点路由
     *
     * @param rpcReq
     * @param ctx
     * @throws InterruptedException
     */
    private void endRouterHandle(RpcReq rpcReq, ChannelHandlerContext ctx) throws InterruptedException {
        //扫描被调用服务下的节点，选取一个对应appName的channel
        Channel channel = ChannelHolderForRouter.getInstance().findChannel(rpcReq);
        //获取原msgId
        String oldMsgId = rpcReq.getMessageId();
        //设置新的msgId
        rpcReq.setMessageId(UUID.randomUUID().toString());
        //发送消息 同步等待应答
        BaseMessage response = remotingServer.sendMessage(channel, rpcReq);
        if (null == response) {
            ctx.channel().writeAndFlush(MessageProtocol.errorMsg("get Rpc msg timeout",oldMsgId));
            return;
        }
        //设置上个节点的msgId为response的msgId
        response.setMessageId(oldMsgId);
        ctx.channel().writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, response));
    }

    /**
     * 向dispatcher发送寻址请求
     *
     * @param ctx
     * @param baseMessage
     * @return
     * @throws InterruptedException
     */
    private String nextRouterAddr(ChannelHandlerContext ctx, BaseMessage baseMessage) throws InterruptedException {
        RpcReq rpcReq = (RpcReq) baseMessage;
        SelectNextRouterReq selectNextRouterReq = new SelectNextRouterReq();
        selectNextRouterReq.setTargetApp(rpcReq.getAppName());
        BaseMessage result = remotingClient.sendRequest(RouterConfig.getInstance().getDispatcherAddr(), selectNextRouterReq);
        SelectNextRouterResponse selectNextRouterResponse = (SelectNextRouterResponse) result;
        if (null == result || StringUtils.isBlank(selectNextRouterResponse.getNextRouterIp())) {
            ctx.channel().writeAndFlush(MessageProtocol.errorMsg("find next Router timeout",rpcReq.getMessageId()));
            return null;
        }
        return ((SelectNextRouterResponse) result).getNextRouterIp();
    }

    /**
     * caller的握手请求处理(用来注册appName：channel的一个映射，为了方便调用rpc)
     * 同时向dispatcher中去更新router中存储的caller信息
     *
     * @param ctx
     * @param baseMessage
     */
    private BaseMessage handlerCallerHand(ChannelHandlerContext ctx, BaseMessage baseMessage) throws InterruptedException {
        CallerHandMsgReq callerHandMsgReq = (CallerHandMsgReq) baseMessage;
        ChannelHolderForRouter.getInstance().addCaller(ctx, callerHandMsgReq);
        RouterHandMsgResponse routerHandMsgResponse = sendToDispatcher();
        if (null == routerHandMsgResponse) {
            ctx.channel().writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, new CallerHandResponse(false)));
            return baseMessage;
        }
        ctx.channel().writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, new CallerHandResponse(true)));
        return baseMessage;
    }

    /**
     * 到dispatcher更新router信息
     *
     * @return
     * @throws InterruptedException
     */
    private RouterHandMsgResponse sendToDispatcher() throws InterruptedException {
        Set<String> appSet = ChannelHolderForRouter.getInstance().getLatestAppSetOnCurrRouter();
        String localIp = IpUtil.getLocalIp() + ":" + RouterConfig.getInstance().getServerPort();
        RouterHandMsgReq routerHandMsgReq = new RouterHandMsgReq(localIp, appSet);
        return (RouterHandMsgResponse) remotingClient.sendRequest(RouterConfig.getInstance().getDispatcherAddr(), routerHandMsgReq);
    }
}
