package com.zqr.study.spring.framework.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZqrAutowired {
    String value() default "";
}
