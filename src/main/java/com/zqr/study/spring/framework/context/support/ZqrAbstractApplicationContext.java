package com.zqr.study.spring.framework.context.support;

/**
 * @Description: IOC容器实现的顶层设计
 * @Auther: qingruizhu
 * @Date: 2019-04-18 16:15
 */
public abstract class ZqrAbstractApplicationContext {
    /**
     * @Description: refresh方法，只提供给子类重写
     * @Author qingruizhu
     * @Date 16:17 2019-04-18
     * @Param []
     * @return void
     **/
    protected void refresh() throws Exception{};
}
