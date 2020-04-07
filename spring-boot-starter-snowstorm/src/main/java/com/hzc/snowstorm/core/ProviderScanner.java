package com.hzc.snowstorm.core;

import com.hzc.snowstorm.annotation.MethodId;
import com.rpc.register.service.ServiceProvider;
import com.rpc.register.service.ServiceProvidersHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author: hzc
 * @Date: 2020/04/03  17:25
 * @Description:
 */
public class ProviderScanner extends ClassPathBeanDefinitionScanner {
    private ServiceProvidersHolder serviceProvidersHolder;

    public ProviderScanner(BeanDefinitionRegistry registry, ServiceProvidersHolder serviceProvidersHolder) {
        super(registry, false);
        this.serviceProvidersHolder = serviceProvidersHolder;
    }

    @Override
    protected Set<BeanDefinitionHolder> doScan(String... basePackages) {
        Set<BeanDefinitionHolder> beanDefinitionHolders = super.doScan(basePackages);
        for (BeanDefinitionHolder beanDefinitionHolder : beanDefinitionHolders) {
            BeanDefinition beanDefinition = beanDefinitionHolder.getBeanDefinition();
            String beanClassName = beanDefinition.getBeanClassName();
            resolveProvider(beanClassName);
        }
        return beanDefinitionHolders;
    }

    private void resolveProvider(String providerClassName) {
        Class<?> providerType;
        Object providerInstance;
        try {
            providerType = Class.forName(providerClassName);
            providerInstance = providerType.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
        Method[] methods = providerType.getMethods();
        for (Method method : methods) {
            MethodId methodIdAnnotation = method.getAnnotation(MethodId.class);
            if (methodIdAnnotation == null) {
                continue;
            }
            if (StringUtils.isBlank(methodIdAnnotation.methodId())) {
                throw new RuntimeException("no annotation MethodId");
            }
            String methodId = methodIdAnnotation.methodId();
            ServiceProvider serviceProvider = new ServiceProvider();
            serviceProvider.putProvider(methodId, providerType, method, providerInstance);
            serviceProvidersHolder.addProvider(methodId, serviceProvider);
        }


    }
}
