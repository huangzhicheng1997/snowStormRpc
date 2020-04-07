package com.hzc.snowstorm.annotation;

import com.hzc.snowstorm.core.SnowStormRegisterImporter;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author: hzc
 * @Date: 2020/04/03  18:05
 * @Description:
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(SnowStormRegisterImporter.class)
public @interface SnowStormScanner {
    String callerPackage() default "";

    String providerPackage() default "";
}
