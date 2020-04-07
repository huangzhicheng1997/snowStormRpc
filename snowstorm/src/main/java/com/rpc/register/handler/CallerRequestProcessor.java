package com.rpc.register.handler;

import com.rpc.register.core.event.RpcReqEventListener;
import com.rpc.register.core.event.RpcResponseHandleEvent;
import com.rpc.register.protocol.MessageProtocol;
import com.rpc.register.protocol.body.RpcReq;
import com.rpc.register.protocol.header.BaseMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: hzc
 * @Date: 2020/03/20  17:30
 * @Description: router推送的rpc调用
 */
public class CallerRequestProcessor implements RequestProcessor {
    @Override
    public BaseMessage requestHandle(ChannelHandlerContext ctx, MessageProtocol messageProtocol) {
        if (messageProtocol.getContent() instanceof RpcReq) {
            RpcReq rpcReq = (RpcReq) messageProtocol.getContent();
            try {

                //发布调用事件
                RpcReqEventListener.publishCallEvent(new RpcResponseHandleEvent(rpcReq, ctx.channel()));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return messageProtocol.getContent();
    }
}
