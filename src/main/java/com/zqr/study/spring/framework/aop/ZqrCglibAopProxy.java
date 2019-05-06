package com.zqr.study.spring.framework.aop;

import com.zqr.study.spring.framework.aop.support.ZqrAdvicedSupport;

/**
 * @Description:    cglib动态代理
 * @Auther: qingruizhu
 * @Date: 2019-04-30 16:13
 */
public class ZqrCglibAopProxy implements ZqrAopProxy {

    private ZqrAdvicedSupport config;


    public ZqrCglibAopProxy(ZqrAdvicedSupport config) {
        this.config = config;
    }

    public Object getProxy() {
        return null;
    }

    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
