package com.hzc.rpc.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * @author: hzc
 * @Date: 2020/04/03  14:42
 * @Description:
 */
public class ServiceProvidersHolder {

    private final ConcurrentMap<String/*methodId*/, ServiceProvider> providerMap = new ConcurrentHashMap<>();

    /**
     * 添加Provider
     *
     * @param methodId
     * @param serviceProvider
     */
    public void addProvider(String methodId, ServiceProvider serviceProvider) {
        providerMap.put(methodId, serviceProvider);
    }

    /**
     * 执行MethodId下的ServiceProvider的对应方法
     *
     * @param args
     * @param methodId
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
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

    /**
     * 获取Provider类型集合
     *
     * @return
     */
    public List<Class<?>> getProviderTypes() {
        List<Class<?>> providerClasses = new ArrayList<>();
        providerMap.forEach((methodId, serviceProvider) -> providerClasses.add(serviceProvider.getProviderType()));
        return providerClasses;
    }

    /**
     * 刷新provider 把对象置换为springBean
     *
     * @param springInstance
     */
    public void refreshProvider(List<?> springInstance) {
        Map<? extends Class<?>, ServiceProvider> serviceProviderMap = providerMap.values().stream().collect(Collectors.toMap(ServiceProvider::getProviderType, serviceProvider -> serviceProvider));

        springInstance.forEach(inst -> {
            ServiceProvider serviceProvider = serviceProviderMap.get(inst.getClass());
            if (serviceProvider != null) {
                serviceProvider.setInstance(inst);
            }
        });
    }


}
