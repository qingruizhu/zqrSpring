package com.zqr.study.spring.framework.aop.intercept;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-30 17:49
 */
public class ZqrMethodInvocation implements ZqrJoinPoint{

    private Object proxy;
    private Method method;
    private Object target;
    private Class<?> targetClass;
    private Object[] arguments;
    private List<Object> interceptorsAndDynamicMethodMachers;
    private int currentInterceptorIndex = -1;

    public ZqrMethodInvocation(Object proxy, Method method, Object target, Class<?> targetClass, Object[] arguments, List<Object> interceptorsAndDynamicMethodMachers) {
        this.proxy = proxy;
        this.method = method;
        this.target = target;
        this.targetClass = targetClass;
        this.arguments = arguments;
        this.interceptorsAndDynamicMethodMachers = interceptorsAndDynamicMethodMachers;
    }


    public Object proceed() throws Throwable {
        if (this.currentInterceptorIndex == this.interceptorsAndDynamicMethodMachers.size()-1){
            return method.invoke(this.target, this.arguments);
        }
        Object interceptorOrInterceptionAdvice = this.interceptorsAndDynamicMethodMachers.get(++currentInterceptorIndex);
        if (interceptorOrInterceptionAdvice instanceof ZqrMethodInterceptor) {
            ZqrMethodInterceptor methodInterceptor = (ZqrMethodInterceptor) interceptorOrInterceptionAdvice;
            return methodInterceptor.invoke(this);
        }else {
            return proceed();
        }
    }

    @Override
    public Method getMethod() {
        return this.method;
    }
    @Override
    public Object[] getArguments() {
        return this.arguments;
    }
    @Override
    public Object getThis() {
        return this.target;
    }
}
