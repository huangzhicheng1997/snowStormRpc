package com.hzc.rpc.core;

import com.hzc.rpc.protocol.body.CallerHandMsgReq;
import com.hzc.rpc.protocol.body.RpcReq;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author: hzc
 * @Date: 2020/01/07  13:46
 * @Description:
 */
public class ChannelHolderForRouter {
    /**
     * 调用者channelTable
     * addr:channel
     */
    private final ConcurrentMap<String/*addr*/, Pair<String/*appName*/, Channel>> callerChannelTable = new ConcurrentHashMap<>();

    private ChannelHolderForRouter() {

    }

    private static class Instance {
        private static final ChannelHolderForRouter channelHolderForRouter = new ChannelHolderForRouter();
    }

    public static ChannelHolderForRouter getInstance() {
        return Instance.channelHolderForRouter;
    }


    /**
     * 根据rpc调用请求 获取随机节点的channel
     *
     * @param rpcReq
     * @return
     */
    public  Channel findChannel(RpcReq rpcReq) {
        String appName = rpcReq.getAppName();
        List<Pair<String, Channel>> pairs = callerChannelTable.values().stream()
                .filter(pair -> pair.getObject1().equals(appName))
                .collect(Collectors.toList());
        if (pairs.size() == 0) {
            return null;
        }
        Random random=new Random();
        return pairs.get(random.nextInt(pairs.size())).getObject2();
    }

    /**
     * hash MessageId得到一个随机数
     *
     * @param messageId
     * @param boundary
     * @return
     */
    private Integer hashMessage(String messageId, Integer boundary) {
        return Arrays.hashCode(messageId.getBytes()) % boundary;
    }


    /**
     * 添加Caller节点信息
     *
     * @param ctx
     * @param callerHandMsgReq
     */
    public void addCaller(ChannelHandlerContext ctx, CallerHandMsgReq callerHandMsgReq) {
        String appName = callerHandMsgReq.getAppName();
        String addr = ctx.channel().remoteAddress().toString();
        Pair<String, Channel> nodeInfo = new Pair<>();
        nodeInfo.put(appName, ctx.channel());
        callerChannelTable.put(addr, nodeInfo);
    }

    /**
     * 获取当前路由器最新的应用集合
     *
     * @return
     */
    public  Set<String> getLatestAppSetOnCurrRouter() {
        Set<Map.Entry<String, Pair<String, Channel>>> entries = callerChannelTable.entrySet();
        return entries.stream().map(Map.Entry::getValue).map(Pair::getObject1).collect(Collectors.toSet());
    }

    /**
     * 节点下线释放资源
     *
     * @param node
     */
    public  void connectionDown(ChannelHandlerContext node) {
        callerChannelTable.remove(node.channel().remoteAddress().toString());
    }

    /**
     * 获取所有的Caller节点
     *
     * @return
     */
    public Set<String> getAllCallerNode() {
        return callerChannelTable.keySet();
    }

}

