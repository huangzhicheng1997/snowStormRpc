package com.rpc.register.service;

import java.lang.reflect.Method;

/**
 * @author: hzc
 * @Date: 2020/04/03  11:50
 * @Description:
 */
public class ServiceProvider {

    /**
     * Provider提供的方法
     */
    private volatile String methodId;

    /**
     * Provider类型
     */
    private volatile Class<?> providerType;

    /**
     * 执行实例 （无状态）
     */
    private volatile Object instance;

    /**
     * methodId对应的方法
     */
    private volatile Method method;

    /**
     * 互斥锁
     */
    private final Object mutex = new Object();

    public void putProvider(String methodId, Class<?> providerType, Method method,Object instance) {
        synchronized (mutex) {
            this.methodId = methodId;
            this.providerType = providerType;
            this.method = method;
            this.instance=instance;
        }
    }


    public String getMethodId() {
        return methodId;
    }


    public Class<?> getProviderType() {
        return providerType;
    }


    public Method getMethod() {
        return method;
    }


    public Object getInstance() {
        return instance;
    }

}
