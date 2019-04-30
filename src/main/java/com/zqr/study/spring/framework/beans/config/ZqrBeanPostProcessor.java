package com.zqr.study.spring.framework.beans.config;

/**
 * @Description: bean后置处理器
 * @Auther: qingruizhu
 * @Date: 2019-04-19 15:13
 */
public class ZqrBeanPostProcessor {
    /**
     * @Description: bean初始化之前提供回调入口
     * @Author qingruizhu
     * @Date 15:16 2019-04-19
     * @Param [bean, beanName]
     * @return java.lang.Object
     **/
    public Object postProcessBeforeInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }
    public Object postProcessAfterInitialization(Object bean,String beanName) throws Exception{
        return bean;
    }
}
