package com.rpc.register.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author: hzc
 * @Date: 2020/04/03  14:42
 * @Description:
 */
public class ServiceProvidersHolder {

    private final ConcurrentMap<String/*methodId*/, ServiceProvider> providerMap = new ConcurrentHashMap<>();

    public void addProvider(String methodId, ServiceProvider serviceProvider) {
        providerMap.put(methodId, serviceProvider);
    }

    public Object invokeProviderMethod(List<Object> args, String methodId) throws InvocationTargetException, IllegalAccessException {
        if (StringUtils.isBlank(methodId)) {
            return null;
        }
        ServiceProvider serviceProvider = providerMap.get(methodId);
        Method method = serviceProvider.getMethod();
        if (CollectionUtils.isEmpty(args)) {
            return method.invoke(serviceProvider.getInstance());
        }
        return method.invoke(serviceProvider.getInstance(), args.toArray());
    }


}
