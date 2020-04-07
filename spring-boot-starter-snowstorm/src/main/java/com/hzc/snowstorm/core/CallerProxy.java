package com.hzc.snowstorm.core;

import com.hzc.snowstorm.annotation.MethodId;
import com.rpc.register.common.MessageIdCode;
import com.rpc.register.function.CallerRemote;
import com.rpc.register.protocol.body.RpcReq;
import com.rpc.register.protocol.body.RpcResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @author: hzc
 * @Date: 2020/04/03  18:15
 * @Description:
 */
public class CallerProxy implements InvocationHandler {

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
        RpcResponse rpcResponse = GlobalConstant.callerRemote.remoteCall(rpcReq);
        if (rpcResponse==null){
            throw new RuntimeException("remote call timeout");
        }
        if (rpcResponse.getRpcResult().equals(0)){
            throw new RuntimeException("not find target service");
        }

        return rpcResponse.getRpcResult();
    }

}
