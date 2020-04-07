package com.rpc.register.protocol.body;

import com.rpc.register.common.RequestCode;
import com.rpc.register.protocol.header.BaseMessage;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author: hzc
 * @Date: 2020/03/18  19:01
 * @Description:
 */
public class RpcReq extends BaseMessage {
    private String appName;

    private AtomicLong routeLength = new AtomicLong();

    private List<Object> args;

    private String methodId;

    public RpcReq() {
        setRouteLength(1);
        setmCode(RequestCode.RPC_MSG);
    }


    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getRouteLengthLong() {
        return routeLength.get();
    }

    public AtomicLong getRouteLength() {
        return routeLength;
    }

    public void setRouteLength(long routeLength) {
        this.routeLength.set(routeLength);
    }

    public void setRouteLength(AtomicLong routeLength) {
        this.routeLength = routeLength;
    }

    public long addAndGetRouteLength() {
        return routeLength.addAndGet(1);
    }

    public List<Object> getArgs() {
        return args;
    }

    public void setArgs(List<Object> args) {
        this.args = args;
    }

    public String getMethodId() {
        return methodId;
    }

    public void setMethodId(String methodId) {
        this.methodId = methodId;
    }
}
