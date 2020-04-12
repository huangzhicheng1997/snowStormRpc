package com.hzc.common.environment;


import java.io.IOException;
import java.io.InputStream;

/**
 * @author: hzc
 * @Date: 2020/04/12  20:09
 * @Description:
 */
public interface Environment {

    /**
     * 设置Resource
     *
     * @param resource
     */
    void setResource(Resource resource);

    /**
     * 获取配置的值
     *
     * @param key key
     * @return
     */
    String getPropertyValue(String key);

    /**
     * 获取文件字节流
     *
     * @return
     */
    InputStream getResourceAsInputStream();

    /**
     * 加载配置文件
     *
     * @throws IOException
     */
    void loadProperties() throws IOException;


}
