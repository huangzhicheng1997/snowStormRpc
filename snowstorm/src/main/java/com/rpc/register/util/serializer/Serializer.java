package com.rpc.register.util.serializer;

/**
 * @author: hzc
 * @Date: 2020/03/05  11:29
 * @Description:
 */
public interface Serializer {
    /**
     * 序列化
     *
     * @param object
     * @param bytes
     */
    void serializer(Object object, byte[] bytes);

    /**
     * 序列化
     *
     * @param object
     * @param bytes
     * @param offset
     * @param length
     */
    void serializer(Object object, byte[] bytes, int offset, int length);

    /**
     * 反序列化
     *
     * @param bytes
     * @param <T>
     * @return
     */
    <T> T deserializer(byte[] bytes);

    /**
     * 反序列化
     *
     * @param bytes
     * @param offset
     * @param length
     * @param <T>
     * @return
     */
    <T> T deserializer(byte[] bytes, int offset, int length);
}
