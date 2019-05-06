package com.zqr.study.spring.framework.aop.aspect;

import com.zqr.study.spring.framework.aop.intercept.ZqrJoinPoint;
import com.zqr.study.spring.framework.aop.intercept.ZqrMethodInterceptor;
import com.zqr.study.spring.framework.aop.intercept.ZqrMethodInvocation;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-05-05 14:06
 */
public class ZqrAfterThrowingAdvice extends ZqrAbstractAspectJAdvice implements ZqrMethodInterceptor {

    private ZqrJoinPoint joinPoint;
    public ZqrAfterThrowingAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(ZqrMethodInvocation methodInvocation) throws Throwable {
        try {
            return methodInvocation.proceed();
        } catch (Throwable throwable) {
            invokeAdviceMethod(methodInvocation, null, throwable);
            throw throwable;
        }

    }

}
