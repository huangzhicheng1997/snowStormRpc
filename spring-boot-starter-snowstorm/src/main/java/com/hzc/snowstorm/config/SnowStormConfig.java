package com.hzc.snowstorm.config;

import com.hzc.rpc.function.CallerRemote;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import static com.hzc.snowstorm.config.RemoteCallContext.callerRemote;
import static com.hzc.snowstorm.config.RemoteCallContext.refresh;

/**
 * @author: hzc
 * @Date: 2020/04/03  17:19
 * @Description:
 */
@Configuration
public class SnowStormConfig implements ApplicationContextAware, ApplicationListener<ContextRefreshedEvent> {
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
        //新建callerRemote实例，并绑定一个服务名
        CallerRemote callerRemote = new CallerRemote(appName);
        //
        callerRemote.setServiceProvidersHolder(RemoteCallContext.serviceProvidersHolder);
        callerRemote.setDispatcherAddr(dispatcherAddr);
        if (maxThread.equals("0")) {
            maxThread = queueNumbers;
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
        //刷新容器
        refresh(context);
        //开启Rpc客户端
        callerRemote = context.getBean(CallerRemote.class);
        callerRemote.start();
    }


}
