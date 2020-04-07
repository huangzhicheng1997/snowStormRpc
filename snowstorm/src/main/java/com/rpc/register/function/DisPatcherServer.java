package com.rpc.register.function;

import com.rpc.register.common.RequestCode;
import com.rpc.register.common.ServerType;
import com.rpc.register.config.DispatcherConfig;
import com.rpc.register.config.ServerConfig;
import com.rpc.register.core.Pair;
import com.rpc.register.handler.DispatcherCloseChannelStrategy;
import com.rpc.register.handler.RequestProcessor;
import com.rpc.register.handler.ServerRequestProcessor;
import com.rpc.register.remote.RemotingServer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author: hzc
 * @Date: 2020/03/21  20:54
 * @Description: 分发器
 */
public class DisPatcherServer {
    private RemotingServer remotingServer;

    public DisPatcherServer() {
        ExecutorService executorService = Executors.newFixedThreadPool(8);
        this.remotingServer = new RemotingServer();
        remotingServer.setServerType(ServerType.DISPATCHER);
        remotingServer.setCloseChannelStrategy(new DispatcherCloseChannelStrategy());
        ServerRequestProcessor serverRequestProcessor = new ServerRequestProcessor(remotingServer, null);
        Pair<RequestProcessor, ExecutorService> pair = new Pair<>();
        pair.put(serverRequestProcessor, executorService);
        remotingServer.registerProcesser(RequestCode.REGISTE_ROUTERS, pair);
        remotingServer.registerProcesser(RequestCode.FIND_NEXT_ROUTER, pair);
        remotingServer.registerProcesser(RequestCode.LOAD_BALANCE,pair);
        remotingServer.setCloseChannelStrategy(new DispatcherCloseChannelStrategy());
    }


    public void start() {
        remotingServer.start();
    }

    public void setServerPort(Integer port) {
        DispatcherConfig.getInstance().setServerPort(port);
    }
}
