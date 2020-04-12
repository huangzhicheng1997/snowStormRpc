package com.hzc.rpc.startTest;

import com.hzc.rpc.function.RouterServer;

/**
 * @author: hzc
 * @Date: 2020/03/30  11:21
 * @Description:
 */
public class RouterStart {

    public static void main(String[] args) {
        RouterServer routerServer = new RouterServer();
        routerServer.setDisPatcherAddr("localhost:8090");
        routerServer.setServerPort(8081);
        routerServer.start();
    }
}
