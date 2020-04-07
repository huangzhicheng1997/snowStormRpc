package com.hzc.snowstorm.core;

import com.hzc.snowstorm.annotation.SnowCaller;
import com.hzc.snowstorm.annotation.SnowProvider;
import com.hzc.snowstorm.annotation.SnowStormScanner;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;

/**
 * @author: hzc
 * @Date: 2020/04/03  17:22
 * @Description:
 */
public class SnowStormRegisterImporter implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        registerProvider(importingClassMetadata, registry);
        registerCaller(importingClassMetadata, registry);


    }

    private void registerProvider(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(SnowStormScanner.class.getName()));
        String basePackage = annotationAttributes.getString("providerPackage");
        if (StringUtils.isBlank(basePackage)){
            return;
        }
        ProviderScanner providerScanner = new ProviderScanner(registry, GlobalConstant.serviceProvidersHolder);
        providerScanner.addIncludeFilter(new AnnotationTypeFilter(SnowProvider.class));
        providerScanner.doScan(basePackage);
    }

    private void registerCaller(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(SnowStormScanner.class.getName()));
        String basePackage = annotationAttributes.getString("callerPackage");
        if (StringUtils.isBlank(basePackage)){
            return;
        }
        CallerScanner callerScanner = new CallerScanner(registry);
        callerScanner.addIncludeFilter(new AnnotationTypeFilter(SnowCaller.class));
        callerScanner.doScan(basePackage);
    }
}
