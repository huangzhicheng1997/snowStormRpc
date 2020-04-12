package com.hzc.caller.function;

import com.hzc.rpc.common.RequestCode;
import com.hzc.rpc.core.Pair;
import com.hzc.rpc.core.event.DefaultRpcService;
import com.hzc.rpc.core.event.RpcReqEventListener;
import com.hzc.rpc.handler.CallerRequestProcessor;
import com.hzc.rpc.handler.RequestProcessor;
import com.hzc.rpc.protocol.body.*;
import com.hzc.rpc.protocol.header.BaseMessage;
import com.hzc.rpc.remote.RemotingClient;
import com.hzc.rpc.service.ServiceProvidersHolder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author: hzc
 * @Date: 2020/03/20  15:09
 * @Description: 调用者(Test)
 */
public class CallerRemote {
    /**
     * 客户端连接
     */
    private RemotingClient remotingClient;
    /**
     * 当前服务名
     */
    private String appName;
    /**
     * caller连接的路由器地址 （断线触发）
     */
    private volatile String routerAddr;
    /**
     * 分发器地址
     */
    private String dispatcherAddr;
    /**
     * nettyServer业务线程池
     */
    private ExecutorService commonEventExecutor;
    /**
     * Rpc调用事件监听器
     */
    private RpcReqEventListener rpcReqEventListener;

    private ServiceProvidersHolder serviceProvidersHolder;

    public CallerRemote(String appName) {
        commonEventExecutor = Executors.newFixedThreadPool(8);
        this.remotingClient = new RemotingClient();
        //设置对应请求
        Pair<RequestProcessor, ExecutorService> pair = new Pair<>();
        pair.put(new CallerRequestProcessor(), commonEventExecutor);
        remotingClient.registerProcesser(RequestCode.RPC_MSG, pair);
        this.appName = appName;
    }

    public void setDispatcherAddr(String dispatcherAddr) {
        this.dispatcherAddr = dispatcherAddr;
    }

    public void setServiceProvidersHolder(ServiceProvidersHolder serviceProvidersHolder) {
        this.serviceProvidersHolder = serviceProvidersHolder;
    }


    public void setRpcReqEventListener(Integer queueNumbers, Integer queueCapacity, Integer maxThread) {
        rpcReqEventListener = new RpcReqEventListener(queueNumbers, queueCapacity, maxThread);
        if (serviceProvidersHolder == null) {
            throw new RuntimeException("no service provider Definition");
        }
        rpcReqEventListener.setRpcService(new DefaultRpcService(serviceProvidersHolder));
    }


    public void start() {
        //开启boss线程
        remotingClient.start();
        //开启消息处理线程模型
        rpcReqEventListener.eventExecuteAndNotify();
        //分发器获取routerAddr
        this.routerAddr = findTargetRouterFromDispatcher();
        //发送握手消息
        if (!sendCallerHandReq()) {
            throw new RuntimeException("握手失败");
        }
        //定时发送握手消息
        heartBeat();
    }

    private boolean sendCallerHandReq() {
        CallerHandResponse callerHandResponse = null;
        //发送握手请求
        try {
            callerHandResponse = (CallerHandResponse) remotingClient.sendRequest(routerAddr, new CallerHandMsgReq(appName));
        } catch (Exception e) {
            System.out.println("握手失败");
        }
        if (callerHandResponse == null || !callerHandResponse.isSuccess()) {
            return false;
        }
        return true;
    }

    private String findTargetRouterFromDispatcher() {
        String routerAddr = null;
        try {
            BalanceResponse balanceResponse = (BalanceResponse) remotingClient.sendRequest(dispatcherAddr, new BalanceReq());
            routerAddr = balanceResponse.getRouterAddr();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return routerAddr;
    }


    public RpcResponse remoteCall(RpcReq rpcReq) throws InterruptedException {
        BaseMessage baseMessage = remotingClient.sendRequest(routerAddr, rpcReq);
        if (baseMessage instanceof RpcResponse){
            return (RpcResponse) baseMessage;
        }else {
            return null;
        }
    }


    /**
     * 5秒一次心跳
     *
     * @param
     */
    private void heartBeat() {
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
        scheduledThreadPoolExecutor.scheduleAtFixedRate(this::sendCallerHandReq, 5, 5, TimeUnit.SECONDS);
    }

}
