package com.rpc.register.protocol;

import com.rpc.register.protocol.header.BaseMessage;
import com.rpc.register.util.CodecUtil;
import com.rpc.register.util.serializer.KryoSerializer;

import java.nio.charset.StandardCharsets;

/**
 * @author: hzc
 * @Date: 2020/03/17  14:20
 * @Description:
 */
public  class MessageSerializer {
    /**
     * 序列化消息协议的body
     *
     * @return
     */
    public static byte[] getProtocolBody(Object obj) {
        try {
            byte[] classNameStream = obj.getClass().getName().getBytes();
            int classNameLength = classNameStream.length;
            byte[] classNameLengthStream = CodecUtil.intToByteArray(classNameLength);

            KryoSerializer kryoSerializer = new KryoSerializer(obj.getClass());
            byte[] serializer = kryoSerializer.serializer(obj);

            byte[] protocolBodyStream = new byte[classNameStream.length + classNameLengthStream.length + serializer.length];
            System.arraycopy(classNameLengthStream, 0, protocolBodyStream, 0, classNameLengthStream.length);
            System.arraycopy(classNameStream, 0, protocolBodyStream, classNameLengthStream.length, classNameStream.length);

            System.arraycopy(serializer, 0, protocolBodyStream, classNameLengthStream.length + classNameStream.length, serializer.length);

            return protocolBodyStream;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 反序列化消息协议body
     *
     * @param input
     * @return
     */
    public static BaseMessage resolveProtocolBody(byte[] input) {
        byte[] classNameLengthStream = new byte[4];
        System.arraycopy(input, 0, classNameLengthStream, 0, 4);
        int classNameLength = CodecUtil.byteArrayToInt(classNameLengthStream);

        byte[] classNameStream = new byte[classNameLength];
        System.arraycopy(input, 4, classNameStream, 0, classNameLength);
        String className = new String(classNameStream, StandardCharsets.UTF_8);

        byte[] contentStream = new byte[input.length - classNameLength - classNameLengthStream.length];
        System.arraycopy(input, 4 + classNameStream.length, contentStream, 0, contentStream.length);
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        KryoSerializer kryoSerializer = new KryoSerializer(aClass);
        return (BaseMessage) kryoSerializer.deserializer(contentStream);
    }
}
