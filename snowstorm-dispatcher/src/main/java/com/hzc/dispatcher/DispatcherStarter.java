package com.hzc.dispatcher;

import com.hzc.dispatcher.config.DispatcherConfigReader;
import com.hzc.dispatcher.function.DisPatcherServer;

import java.io.IOException;

/**
 * @author: hzc
 * @Date: 2020/04/12  20:06
 * @Description:
 */
public class DispatcherStarter {

    public static void main(String[] args) throws IOException {
        Integer serverPort = DispatcherConfigReader.getServerPort();
        DisPatcherServer disPatcherServer = new DisPatcherServer();
        disPatcherServer.setServerPort(serverPort);
        disPatcherServer.start();
    }




}
