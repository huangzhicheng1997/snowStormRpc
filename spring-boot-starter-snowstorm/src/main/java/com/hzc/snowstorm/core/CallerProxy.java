package com.hzc.snowstorm.core;

import com.hzc.rpc.protocol.body.RpcReq;
import com.hzc.rpc.protocol.body.RpcResponse;
import com.hzc.snowstorm.annotation.MethodId;
import com.hzc.snowstorm.config.RemoteCallContext;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author: hzc
 * @Date: 2020/04/03  18:15
 * @Description:
 */
public class CallerProxy extends RemoteCallContext implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        MethodId methodAnnotation = method.getAnnotation(MethodId.class);
        if (methodAnnotation == null || StringUtils.isBlank(methodAnnotation.callServerName()) || StringUtils.isBlank(methodAnnotation.methodId())) {
            return null;
        }
        RpcReq rpcReq = new RpcReq();
        rpcReq.setMethodId(methodAnnotation.methodId());
        rpcReq.setAppName(methodAnnotation.callServerName());
        rpcReq.setMessageId(UUID.randomUUID().toString());
        rpcReq.setArgs(Arrays.stream(args).collect(Collectors.toList()));
        RpcResponse rpcResponse = callerRemote.remoteCall(rpcReq);
        if (rpcResponse == null) {
            throw new RuntimeException("remote call timeout");
        }
        if (rpcResponse.getRpcResult().equals(0)) {
            throw new RuntimeException("not find target service");
        }

        return rpcResponse.getRpcResult();
    }

}
