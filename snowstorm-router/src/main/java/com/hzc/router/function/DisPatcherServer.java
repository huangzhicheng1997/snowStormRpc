package com.hzc.router.function;




import com.hzc.rpc.common.RequestCode;
import com.hzc.rpc.common.ServerType;
import com.hzc.rpc.config.DispatcherConfig;
import com.hzc.rpc.core.Pair;
import com.hzc.rpc.handler.DispatcherCloseChannelStrategy;
import com.hzc.rpc.handler.RequestProcessor;
import com.hzc.rpc.handler.ServerRequestProcessor;
import com.hzc.rpc.remote.RemotingServer;


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
