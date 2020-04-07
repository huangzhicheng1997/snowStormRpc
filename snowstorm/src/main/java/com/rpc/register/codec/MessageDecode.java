package com.rpc.register.codec;

import com.rpc.register.exceptions.LengthFieldTooLongException;
import com.rpc.register.protocol.MessageSerializer;
import com.rpc.register.protocol.MessageProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * @author: hzc
 * @Date: 2020/03/03  10:19
 * @Description: 自定义解码器
 */
public class MessageDecode extends LengthFieldBasedFrameDecoder {


    public MessageDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }

    public MessageDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    public MessageDecode(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    public MessageDecode(ByteOrder byteOrder, int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment, int initialBytesToStrip, boolean failFast) {
        super(byteOrder, maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip, failFast);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf decodeFrame = (ByteBuf) super.decode(ctx, in);
        if (null==decodeFrame){
            return null;
        }
        //获取type字段值
        int type = decodeFrame.readInt();

        //获取size
        int size = decodeFrame.readInt();
        //校验size是否正确
        int readableBytes = decodeFrame.readableBytes();
        if (readableBytes < size) {
            throw new LengthFieldTooLongException("size not right ,please check size is equal to content length");
        }
        //针对心跳消息和安全认证消息 无body的情况进行解析
        if (readableBytes == 0) {
            return MessageProtocol.getNoBodyMessage(type);
        }

        byte[] body = new byte[size];
        decodeFrame.readBytes(body);
        in.release();
        return new MessageProtocol(type, MessageSerializer.resolveProtocolBody(body));

    }

}
