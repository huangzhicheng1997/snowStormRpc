package com.hzc.router.function;

import com.hzc.rpc.common.RequestCode;
import com.hzc.rpc.common.ServerType;
import com.hzc.rpc.config.RouterConfig;
import com.hzc.rpc.core.Pair;
import com.hzc.rpc.handler.RequestProcessor;
import com.hzc.rpc.handler.RouterCloseChannelStrategy;
import com.hzc.rpc.handler.ServerRequestProcessor;
import com.hzc.rpc.protocol.body.RouterHandMsgReq;
import com.hzc.rpc.remote.RemotingClient;
import com.hzc.rpc.remote.RemotingServer;
import com.hzc.rpc.util.IpUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: hzc
 * @Date: 2020/03/19  15:25
 * @Description: 路由器
 */
public class RouterServer {

    private ExecutorService executorService;
    private RemotingServer remotingServer;
    private RemotingClient remotingClient;

    private String disPatcherAddr;

    public RouterServer() {
        remotingClient = new RemotingClient();
        remotingServer = new RemotingServer();
        remotingServer.setServerType(ServerType.ROUTER);
        executorService = Executors.newFixedThreadPool(8);
        ServerRequestProcessor serverRequestProcessor = new ServerRequestProcessor(remotingServer, remotingClient);
        Pair<RequestProcessor, ExecutorService> pair = new Pair<>();
        pair.put(serverRequestProcessor, executorService);
        //为服务端注册request处理脚本
        remotingServer.registerProcesser(RequestCode.RPC_MSG, pair);
        remotingServer.registerProcesser(RequestCode.CALLER_AUTH, pair);
        remotingServer.setCloseChannelStrategy(new RouterCloseChannelStrategy());
    }


    public void start() {
        remotingClient.start();
        remotingServer.start();
        handShakeToDispatcher();
    }

    private void handShakeToDispatcher() {
        RouterHandMsgReq routerHandMsgReq = new RouterHandMsgReq();
        routerHandMsgReq.setAddr(IpUtil.getLocalIp() + ":" + RouterConfig.getInstance().getServerPort());
        try {
            remotingClient.sendRequest(disPatcherAddr, routerHandMsgReq);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String getDisPatcherAddr() {
        return disPatcherAddr;
    }

    public void setDisPatcherAddr(String disPatcherAddr) {
        this.disPatcherAddr = disPatcherAddr;
        RouterConfig.getInstance().setDispatcherAddr(disPatcherAddr);
    }

    public void setServerPort(Integer port) {
        RouterConfig.getInstance().setServerPort(port);
    }

}
