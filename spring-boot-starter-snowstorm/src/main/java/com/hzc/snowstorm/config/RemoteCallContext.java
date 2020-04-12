package com.hzc.snowstorm.config;


import com.hzc.caller.function.CallerRemote;
import com.hzc.rpc.service.ServiceProvidersHolder;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author: hzc
 * @Date: 2020/04/08  09:33
 * @Description:
 */
public class RemoteCallContext {
    protected static ServiceProvidersHolder serviceProvidersHolder = new ServiceProvidersHolder();

    protected static CallerRemote callerRemote;

    /**
     * 刷新容器
     *
     * @param context
     */
    public static void refresh(ApplicationContext context) {

        //1. 把caller客户端 放入全局变量中
        RemoteCallContext.callerRemote = context.getBean(CallerRemote.class);

        //2.刷新ServiceProvidersHolder持有的Provider的instance属性，将其置为springBean
        ServiceProvidersHolder serviceProvidersHolder = RemoteCallContext.serviceProvidersHolder;
        List<Class<?>> providerTypes = serviceProvidersHolder.getProviderTypes();
        providerTypes.forEach(aClass -> {
            Map<String, ?> beansOfType = context.getBeansOfType(aClass);
            List<?> beans = new ArrayList<>(beansOfType.values());
            serviceProvidersHolder.refreshProvider(beans);
        });

    }


}
