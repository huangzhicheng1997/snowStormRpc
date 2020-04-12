package com.hzc.rpc.startTest;

import com.hzc.rpc.function.DisPatcherServer;

/**
 * @author: hzc
 * @Date: 2020/03/30  11:21
 * @Description:
 */
public class DispatcherStarter {

    public static void main(String[] args) {
        DisPatcherServer disPatcherServer = new DisPatcherServer();
        disPatcherServer.setServerPort(8090);
        disPatcherServer.start();
    }
}
