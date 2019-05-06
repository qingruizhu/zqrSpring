package com.zqr.study.spring.framework.context;

import com.zqr.study.spring.framework.annotation.ZqrAutowired;
import com.zqr.study.spring.framework.annotation.ZqrController;
import com.zqr.study.spring.framework.aop.ZqrAopConfig;
import com.zqr.study.spring.framework.aop.ZqrAopProxy;
import com.zqr.study.spring.framework.aop.ZqrCglibAopProxy;
import com.zqr.study.spring.framework.aop.ZqrJdkDynamicAopProxy;
import com.zqr.study.spring.framework.aop.support.ZqrAdvicedSupport;
import com.zqr.study.spring.framework.beans.ZqrBeanWrapper;
import com.zqr.study.spring.framework.beans.config.ZqrBeanDefinition;
import com.zqr.study.spring.framework.beans.config.ZqrBeanPostProcessor;
import com.zqr.study.spring.framework.beans.support.ZqrBeanDefinitionReader;
import com.zqr.study.spring.framework.beans.support.ZqrDefaultListableBeanFactory;
import com.zqr.study.spring.framework.core.ZqrBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Description: 核心容器   IOC->DI->MVC->AOP
 * @Auther: qingruizhu
 * @Date: 2019-04-18 16:24
 */
public class ZqrApplicationContext extends ZqrDefaultListableBeanFactory implements ZqrBeanFactory {

    private String[] configLocations;
    private ZqrBeanDefinitionReader reader;

    //单例的IOC容器缓存
    private Map<String, Object> singletonBeanCacheMap = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器:存储所有的被代理过的对象
    private Map<String, ZqrBeanWrapper> beanWrapperMap = new ConcurrentHashMap<String, ZqrBeanWrapper>();

    public ZqrApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            this.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void refresh() throws Exception {
        //1.定位，把配置文件定位到BeanDefinitionReader里面
        reader = new ZqrBeanDefinitionReader(this.configLocations);
        //2.加载，加载配置文件，扫描相关的类，把他们封装成BeanDefinition
        List<ZqrBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();
        //3.注册，把配置信息放到容器里面（伪IOC容器）
        doRegisterBeanDefinition(beanDefinitions);
        //4.把不是延时加载的类，提前初始化
        doAutoWired();

        System.out.println("hahahahaah init over !!!");
    }

    private void doAutoWired() throws Exception {
        Set<Map.Entry<String, ZqrBeanDefinition>> entries = super.beanDefinitionMap.entrySet();
        for (Map.Entry<String, ZqrBeanDefinition> entry : entries) {
            ZqrBeanDefinition definition = entry.getValue();
            if (!definition.isLazyInit()) {
                getBean(entry.getKey());
            }
        }
    }

    private void doRegisterBeanDefinition(List<ZqrBeanDefinition> beanDefinitions) throws Exception {
        for (ZqrBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + " is exists!!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    //依赖注入，从这里开始，通过读取 BeanDefinition 中的信息
    //然后，通过反射机制创建一个实例并返回
    //Spring 做法是，不会把最原始的对象放出去，会用一个 BeanWrapper 来进行一次包装
    //装饰器模式:
    //1、保留原来的 OOP 关系
    //2、我需要对它进行扩展，增强(为了以后 AOP 打基础)
    public Object getBean(String beanName) {
        ZqrBeanDefinition definition = this.beanDefinitionMap.get(beanName);
        try {
            //bean实例化，并放入单例容器中
            Object instance = instantiateBean(definition);
            if (null == instance) {
                return null;
            }
            //生成通知事件
            ZqrBeanPostProcessor beanPostProcessor = new ZqrBeanPostProcessor();
            //通知事件，实例初始化之前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);
            //beanWrapper放入wrapper容器中
            ZqrBeanWrapper zqrBeanWrapper = new ZqrBeanWrapper(instance);
            this.beanWrapperMap.put(beanName, zqrBeanWrapper);
            System.out.println("BeanWrapperIOC put " + beanName + " success !");
            //通知事件，实例话之后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);
            //属性注入
            populateBean(instance);
            //从通用的容器中获取，留取可操作空间
            return this.beanWrapperMap.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            //e.fillInStackTrace();
            return null;
        }

    }

    private void populateBean(Object instance) {
        Class<?> clzz = instance.getClass();
        if (!(clzz.isAnnotationPresent(ZqrAutowired.class) || clzz.isAnnotationPresent(ZqrController.class))) return;
        Field[] fields = clzz.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ZqrAutowired.class)) {
                ZqrAutowired autowired = field.getAnnotation(ZqrAutowired.class);
                String autowiredBeanName = autowired.value().trim();
                if ("".equals(autowiredBeanName)) {
                    autowiredBeanName = field.getType().getName();
                }
                //属性强行访问
                field.setAccessible(true);
                try {
                    field.set(instance, this.beanWrapperMap.get(autowiredBeanName).getWrappedInstance());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private Object instantiateBean(ZqrBeanDefinition definition) {
        Object instance = null;
        try {
            if (this.singletonBeanCacheMap.containsKey(definition.getFactoryBeanName())) {
                instance = this.singletonBeanCacheMap.get(definition.getFactoryBeanName());
            } else {
                Class<?> clzz = Class.forName(definition.getBeanClassName());
                instance = clzz.newInstance();
                ZqrAdvicedSupport advicedSupport = instantionAopConfig(definition);
                advicedSupport.setTarget(instance);
                advicedSupport.setTargetClass(clzz);
                if (advicedSupport.pointCutMatch()) {
                    instance = createProxy(advicedSupport).getProxy();
                }
                singletonBeanCacheMap.put(definition.getFactoryBeanName(), instance);
                System.out.println("SingletonIOC put " + definition.getFactoryBeanName() + " success !");
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return instance;
    }

    private ZqrAopProxy createProxy(ZqrAdvicedSupport advicedSupport) {
        Class targetClass = advicedSupport.getTargetClass();
        if (targetClass.getInterfaces().length > 0) {
            return new ZqrJdkDynamicAopProxy(advicedSupport);
        }
        return new ZqrCglibAopProxy(advicedSupport);
    }
    private ZqrAdvicedSupport instantionAopConfig(ZqrBeanDefinition beanDefinition) throws Exception{
        ZqrAopConfig aopConfig = new ZqrAopConfig();
        aopConfig.setPointCut(reader.getConfig().getProperty("pointCut"));
        aopConfig.setAspectClass(reader.getConfig().getProperty("aspectClass"));
        aopConfig.setAspectBefore(reader.getConfig().getProperty("aspectBefore"));
        aopConfig.setAspectAfter(reader.getConfig().getProperty("aspectAfter"));
        aopConfig.setAspectAfterThrow(reader.getConfig().getProperty("aspectAfterThrow"));
        aopConfig.setAspectAfterThrowingName(reader.getConfig().getProperty("aspectAfterThrowingName"));
        return new ZqrAdvicedSupport(aopConfig);
    }

    public Object getBean(Class<?> beanClass) {
        return getBean(beanClass.getName());
    }

    public String[] getBeanDefinitionNames() {
        Set<String> beanNames = this.beanDefinitionMap.keySet();
        String[] beanNameArry = beanNames.toArray(new String[this.beanDefinitionMap.size()]);
        return beanNameArry;
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return reader.getConfig();
    }

}
