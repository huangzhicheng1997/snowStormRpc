package com.rpc.register.handler;

import com.rpc.register.protocol.MessageProtocol;
import com.rpc.register.protocol.header.BaseMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: hzc
 * @Date: 2020/03/19  09:34
 * @Description:
 */
public interface RequestProcessor {
    BaseMessage requestHandle(ChannelHandlerContext ctx, MessageProtocol messageProtocol);
}
