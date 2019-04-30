package com.zqr.study.spring.framework.beans;

/**
 * @Description: bean的包装类
 * @Auther: qingruizhu
 * @Date: 2019-04-18 16:07
 */
public class ZqrBeanWrapper {
    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public ZqrBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return wrappedInstance;
    }
    /**
     * @Description: 返回代理以后的class,可能会是这个$Proxy0
     * @Author qingruizhu
     * @Date 16:13 2019-04-18
     * @Param []
     * @return java.lang.Class<?>
     **/


    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }


}
