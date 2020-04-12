package com.hzc.router;

import com.hzc.router.config.RouterConfigReader;
import com.hzc.rpc.function.RouterServer;

/**
 * @author: hzc
 * @Date: 2020/03/30  11:21
 * @Description:
 */
public class RouterStarter {

    public static void main(String[] args) {
        String dispatcherAddr = RouterConfigReader.getDispatcherAddr();
        Integer serverPort = RouterConfigReader.getServerPort();
        RouterServer routerServer = new RouterServer();
        routerServer.setDisPatcherAddr(dispatcherAddr);
        routerServer.setServerPort(serverPort);
        routerServer.start();
    }
}
