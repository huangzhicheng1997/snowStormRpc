package com.rpc.register.protocol.body;

import com.rpc.register.protocol.header.BaseMessage;

/**
 * @author: hzc
 * @Date: 2020/03/20  16:19
 * @Description:
 */
public class RpcResponse extends BaseMessage {
    private Object rpcResult;

    public Object getRpcResult() {
        return rpcResult;
    }

    public void setRpcResult(Object rpcResult) {
        this.rpcResult = rpcResult;
    }
}
