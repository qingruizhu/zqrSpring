package com.zqr.study.spring.framework.aop.intercept;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-30 17:57
 */
public interface ZqrJoinPoint {
    Method getMethod();
    Object[] getArguments();
    Object getThis();

}
