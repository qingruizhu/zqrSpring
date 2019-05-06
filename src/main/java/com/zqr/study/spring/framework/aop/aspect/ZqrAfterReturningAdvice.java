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
public class ZqrAfterReturningAdvice extends ZqrAbstractAspectJAdvice implements ZqrMethodInterceptor {

    private ZqrJoinPoint joinPoint;
    public ZqrAfterReturningAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(ZqrMethodInvocation methodInvocation) throws Throwable  {
        Object returnValue = methodInvocation.proceed();
        this.joinPoint = methodInvocation;
        this.afterReturning(returnValue,methodInvocation.getMethod(),methodInvocation.getArguments(),methodInvocation.getThis());
        return returnValue;
    }

    private void afterReturning(Object returnValue,Method method, Object[] arguments, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint, returnValue, null);
    }
}
