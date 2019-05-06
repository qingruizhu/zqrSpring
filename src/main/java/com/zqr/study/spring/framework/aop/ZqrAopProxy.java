package com.zqr.study.spring.framework.aop;

/**
 * @Description:
 *          代理接口
 * @Auther: qingruizhu
 * @Date: 2019-04-30 16:11
 */
public interface ZqrAopProxy {

    Object getProxy();

    Object getProxy(ClassLoader classLoader);
}
