package com.zqr.study.spring.framework.aop.intercept;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-05-05 09:42
 */
public interface ZqrMethodInterceptor {

    Object invoke(ZqrMethodInvocation methodInvocation) throws Throwable;
}
