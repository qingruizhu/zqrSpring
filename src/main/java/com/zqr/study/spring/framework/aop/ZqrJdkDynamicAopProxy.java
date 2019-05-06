package com.zqr.study.spring.framework.aop;

import com.zqr.study.spring.framework.aop.intercept.ZqrMethodInvocation;
import com.zqr.study.spring.framework.aop.support.ZqrAdvicedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * @Description:    jdk动态代理
 * @Auther: qingruizhu
 * @Date: 2019-04-30 16:15
 */
public class ZqrJdkDynamicAopProxy implements ZqrAopProxy, InvocationHandler {
    private ZqrAdvicedSupport config;

    public ZqrJdkDynamicAopProxy(ZqrAdvicedSupport config) {
        this.config = config;
    }

    public Object getProxy() {
        return getProxy(this.config.getTargetClass().getClassLoader());
    }

    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.config.getTargetClass().getInterfaces(),this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        List<Object> advices = this.config.getInterceptorsAndDynamicInterceptionAdvice(method,this.config.getTargetClass());
        ZqrMethodInvocation zqrMethodInvocation = new ZqrMethodInvocation(proxy, method, this.config.getTarget(), this.config.getTargetClass(), args, advices);
        return zqrMethodInvocation.proceed();
    }
}
