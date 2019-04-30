package com.zqr.study.spring.framework.core;

/**
 * @Description: 单例工厂的顶层设计
 * @Auther: qingruizhu
 * @Date: 2019-04-18 15:56
 */
public interface ZqrBeanFactory {

    /**
     * @Description: 根据beanName从IOC中获取bean
     * @Author qingruizhu
     * @Date 15:59 2019-04-18
     * @Param [beanName]
     * @return java.lang.Object
     **/
    public Object getBean(String beanName) throws Exception;
    /**
     * @Description: 根据class获取bean
     * @Author qingruizhu
     * @Date 16:00 2019-04-18
     * @Param [beanClass]
     * @return java.lang.Object
     **/
    public Object getBean(Class<?> beanClass) throws Exception;
}
