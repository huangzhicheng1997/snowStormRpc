package com.hzc.rpc.core.event;

import com.hzc.rpc.common.MessageType;
import com.hzc.rpc.protocol.MessageProtocol;
import com.hzc.rpc.protocol.body.RpcReq;
import com.hzc.rpc.protocol.body.RpcResponse;
import io.netty.channel.Channel;

/**
 * @author: hzc
 * @Date: 2020/03/30  11:43
 * @Description:
 */
public class RpcResponseHandleEvent implements CallerHandleEvent {
    /**
     * rpc请求
     */
    private RpcReq rpcReq;

    /**
     * 连接
     */
    private Channel channel;

    public RpcResponseHandleEvent(RpcReq rpcReq, Channel channel) {
        this.rpcReq = rpcReq;
        this.channel = channel;
    }

    @Override
    public void eventNotify(Object rpcResult) {
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setMessageId(rpcReq.getMessageId());
        rpcResponse.setRpcResult(rpcResult);
        channel.writeAndFlush(MessageProtocol.createMessage(MessageType.BUSSINESS_MSG_RES, rpcResponse));
    }

    @Override
    public Channel getChannel() {
        return channel;
    }

    @Override
    public RpcReq getRpcReq() {
        return rpcReq;
    }
}
