package com.hzc.common.environment;

import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;

/**
 * @author: hzc
 * @Date: 2020/04/12  20:17
 * @Description:
 */
public class ClassPathResource implements Resource {

    private ClassLoader classLoader;

    private String fileName;

    @Override
    public void setClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public InputStream getResourceAsInputStream() {
        if (StringUtils.isBlank(fileName) || null == classLoader) {
            return null;
        }

        return classLoader.getResourceAsStream(fileName);
    }

    @Override
    public void setResourceFile(String fileName) {
        this.fileName = fileName;
    }

}
