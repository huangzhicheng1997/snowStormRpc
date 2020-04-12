package com.hzc.common.environment;


import java.io.InputStream;

/**
 * @author: hzc
 * @Date: 2020/04/12  20:11
 * @Description:
 */
public interface Resource {
    /**
     * 设置classLoader
     *
     * @param classLoader
     */
    void setClassLoader(ClassLoader classLoader);

    /**
     * 获取resource字节流
     *
     * @return
     */
    InputStream getResourceAsInputStream();

    /**
     * 设置文件路径
     *
     * @param fileName
     */
    void setResourceFile(String fileName);


}
