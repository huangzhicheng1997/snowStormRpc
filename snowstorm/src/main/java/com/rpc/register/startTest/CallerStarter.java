package com.rpc.register.startTest;

import com.rpc.register.function.CallerRemote;
import com.rpc.register.protocol.body.RpcReq;
import com.rpc.register.protocol.body.RpcResponse;
import com.rpc.register.service.ServiceProvider;
import com.rpc.register.service.ServiceProvidersHolder;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author: hzc
 * @Date: 2020/03/30  11:21
 * @Description:
 */
public class CallerStarter {
    public static void main(String[] args) throws InterruptedException, NoSuchMethodException {
        CallerRemote testApp = new CallerRemote("testApp2");
        testApp.setDispatcherAddr("localhost:8090");

        //配置服务提供类serviceProvidersHolder
        ServiceProvidersHolder serviceProvidersHolder = new ServiceProvidersHolder();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.putProvider("1", TestService.class, TestService.class.getMethod("xx"), new TestService());
        serviceProvidersHolder.addProvider("1", serviceProvider);
        //为Caller配置服务提供实例
        testApp.setServiceProvidersHolder(serviceProvidersHolder);
        testApp.setRpcReqEventListener(2, 50, 4);
        //启动服务
        testApp.start();

        //请求测试
        RpcReq rpcReq = new RpcReq();
        rpcReq.setMessageId(UUID.randomUUID().toString());
        rpcReq.setAppName("testApp");
        rpcReq.setMethodId("1");
        rpcReq.setArgs(null);
        for (int i = 0; ; i++) {
            RpcResponse rpcResponse = testApp.remoteCall(rpcReq);
            System.out.println(rpcResponse.getRpcResult());
            TimeUnit.SECONDS.sleep(1);

        }

        /*CallerRemote testApp = new CallerRemote("testApp");
        testApp.setDispatcherAddr("localhost:8090");
        //配置服务提供类serviceProvidersHolder
        ServiceProvidersHolder serviceProvidersHolder = new ServiceProvidersHolder();
        ServiceProvider serviceProvider = new ServiceProvider();
        serviceProvider.putProvider("1", TestService.class, TestService.class.getMethod("xx"), new TestService());
        serviceProvidersHolder.addProvider("1", serviceProvider);
        //为Caller配置服务提供实例
        testApp.setServiceProvidersHolder(serviceProvidersHolder);
        testApp.setRpcReqEventListener(2,50,4);
        testApp.start();*/

    }

}
