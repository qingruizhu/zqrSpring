package com.zqr.study.spring.framework.aop.aspect;

import com.zqr.study.spring.framework.aop.intercept.ZqrJoinPoint;

import java.lang.reflect.Method;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-05-05 11:17
 */
public abstract class ZqrAbstractAspectJAdvice implements ZqrAdvice{
    private Method aspectMethod;
    private Object aspectTarget;

    public ZqrAbstractAspectJAdvice(Method aspectMethod, Object aspectTarget) {
        this.aspectMethod = aspectMethod;
        this.aspectTarget = aspectTarget;
    }

    protected Object invokeAdviceMethod(ZqrJoinPoint joinPoint,Object returnValue,Throwable ex) throws Throwable{
        Class<?>[] parameterTypes = this.aspectMethod.getParameterTypes();
        if (null == parameterTypes || parameterTypes.length == 0) {
            return this.aspectMethod.invoke(aspectTarget);
        }
        Object[] args = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            if (parameterTypes[i] == ZqrJoinPoint.class) {
                args[i] = joinPoint;
            } else if (parameterTypes[i] == Object.class) {
                args[i] = returnValue;
            } else if (parameterTypes[i] == Throwable.class) {
                args[i] = ex;
            }
        }
        return this.aspectMethod.invoke(this.aspectTarget, args);

    }
}
