package com.zqr.study.spring.framework.beans.support;

import com.zqr.study.spring.framework.beans.config.ZqrBeanDefinition;
import com.zqr.study.spring.framework.context.support.ZqrAbstractApplicationContext;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 可列表化的beanfactory
 * @Auther: qingruizhu
 * @Date: 2019-04-18 16:19
 */
public class ZqrDefaultListableBeanFactory extends ZqrAbstractApplicationContext {
    /**
     * 存储beanDefinition的map
     */
    protected final Map<String, ZqrBeanDefinition> beanDefinitionMap = new HashMap<String, ZqrBeanDefinition>();
}
