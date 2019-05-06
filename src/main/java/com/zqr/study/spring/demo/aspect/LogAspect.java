package com.zqr.study.spring.demo.aspect;

import com.zqr.study.spring.framework.aop.intercept.ZqrJoinPoint;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-05-06 09:59
 */
public class LogAspect {

    public void before(ZqrJoinPoint joinPoint) {
        System.out.println("before....."+joinPoint.getThis()+"..."+joinPoint.getArguments());
    }

    public void after(ZqrJoinPoint joinPoint) {
        System.out.println("after....."+joinPoint.getThis()+"..."+joinPoint.getArguments());
    }

    public void afterThrow(ZqrJoinPoint joinPoint, Throwable ex) {
        System.out.println("异常出现啦:"+ex.getMessage()+"....."+joinPoint.getThis()+"..."+joinPoint.getArguments());
    }
}
