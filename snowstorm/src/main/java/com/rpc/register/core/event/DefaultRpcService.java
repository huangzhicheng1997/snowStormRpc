package com.rpc.register.core.event;

import com.rpc.register.protocol.body.RpcReq;
import com.rpc.register.service.ServiceProvidersHolder;

import java.util.List;

/**
 * @author: hzc
 * @Date: 2020/03/31  18:40
 * @Description:
 */
public class DefaultRpcService implements RpcService {

    private ServiceProvidersHolder serviceProvidersHolder;

    public DefaultRpcService(ServiceProvidersHolder serviceProvidersHolder) {
        this.serviceProvidersHolder = serviceProvidersHolder;
    }

    @Override
    public void executorService(CallerHandleEvent callerHandleEvent) {
        RpcReq rpcReq = callerHandleEvent.getRpcReq();
        List<Object> args = rpcReq.getArgs();
        Object res = null;
        try {
            res = serviceProvidersHolder.invokeProviderMethod(args, rpcReq.getMethodId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        callerHandleEvent.eventNotify(res);

    }
}
