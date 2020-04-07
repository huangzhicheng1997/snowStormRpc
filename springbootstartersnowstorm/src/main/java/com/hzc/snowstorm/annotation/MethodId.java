package com.hzc.snowstorm.annotation;

import java.lang.annotation.*;

/**
 * @author: hzc
 * @Date: 2020/04/03  17:13
 * @Description:
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MethodId {
    String methodId();
    String callServerName() default "";

}
