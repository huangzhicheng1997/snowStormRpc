package com.hzc.snowstorm.config;

import com.hzc.snowstorm.annotation.SnowCaller;
import com.hzc.snowstorm.core.CallerProxy;
import com.hzc.snowstorm.core.GlobalConstant;
import com.rpc.register.function.CallerRemote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author: hzc
 * @Date: 2020/04/03  17:19
 * @Description:
 */
@Configuration
public class SnowStormConfig extends GlobalConstant implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
    @Value("${spring.application.name}")
    private String appName;

    @Value("${snowstorm.dispatcher.addr}")
    private String dispatcherAddr;
    @Value("${snowstorm.queueNumbers}")
    private String queueNumbers;
    @Value("${snowstorm.queueCapacity}")
    private String queueCapacity;
    @Value("${snowstorm.maxThread:0}")
    private String maxThread;

    private static ApplicationContext context;

    @Bean
    public CallerRemote callerRemote() {
        CallerRemote callerRemote = new CallerRemote(appName);
        callerRemote.setServiceProvidersHolder(serviceProvidersHolder);
        callerRemote.setDispatcherAddr(dispatcherAddr);
        if (maxThread.equals("0")){
            maxThread=queueNumbers;
        }
        callerRemote.setRpcReqEventListener(Integer.valueOf(queueNumbers), Integer.valueOf(queueCapacity), Integer.valueOf(maxThread));
        return callerRemote;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        GlobalConstant.callerRemote = context.getBean(CallerRemote.class);
        callerRemote.start();
    }
}
