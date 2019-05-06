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
public class ZqrMethodBeforeAdvice extends ZqrAbstractAspectJAdvice implements ZqrMethodInterceptor {

    private ZqrJoinPoint joinPoint;
    public ZqrMethodBeforeAdvice(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(ZqrMethodInvocation methodInvocation) throws Throwable {
        this.joinPoint = methodInvocation;
        this.before(methodInvocation.getMethod(),methodInvocation.getArguments(),methodInvocation.getThis());
        return methodInvocation.proceed();
    }

    private void before(Method method, Object[] arguments, Object target) throws Throwable {
        invokeAdviceMethod(this.joinPoint, null, null);
    }
}
