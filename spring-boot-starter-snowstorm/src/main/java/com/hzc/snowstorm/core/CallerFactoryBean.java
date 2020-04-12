package com.hzc.snowstorm.core;

import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author: hzc
 * @Date: 2020/04/05  18:34
 * @Description:
 */
public class CallerFactoryBean<T> implements FactoryBean<T> {

    private Class<T> callerClass;



    public CallerFactoryBean(Class<T> callerClass) {
        this.callerClass = callerClass;
    }

    @Override
    public T getObject() throws Exception {
        return (T) Proxy.newProxyInstance(callerClass.getClassLoader(), new Class[]{callerClass}, new CallerProxy());
    }

    @Override
    public Class<T> getObjectType() {
        return callerClass;
    }


}
