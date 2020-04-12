package com.hzc.rpc.handler;

import com.hzc.rpc.protocol.MessageProtocol;
import com.hzc.rpc.protocol.header.BaseMessage;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author: hzc
 * @Date: 2020/03/19  09:34
 * @Description:
 */
public interface RequestProcessor {
    BaseMessage requestHandle(ChannelHandlerContext ctx, MessageProtocol messageProtocol);
}
