package com.hzc.rpc.codec;

import com.hzc.rpc.exceptions.EmptyMsgException;
import com.hzc.rpc.protocol.MessageProtocol;
import com.hzc.rpc.protocol.MessageSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author: hzc
 * @Date: 2020/03/03  10:20
 * @Description: 自定义编码器
 */
public class MessageEncode extends MessageToByteEncoder<MessageProtocol> {

    protected void encode(ChannelHandlerContext ctx, MessageProtocol msg, ByteBuf out) throws Exception {
        if (null == msg) {
            throw new EmptyMsgException();
        }
        //编码type字段
        out.writeInt(msg.getType());
        //编码内容
        Object content = msg.getContent();
        //兼容空消息
        if (null != content) {
            byte[] encodeStream = MessageSerializer.getProtocolBody(content);
            out.writeInt(encodeStream.length);
            out.writeBytes(encodeStream);
        } else {
            out.writeInt(0);
        }
    }
}
