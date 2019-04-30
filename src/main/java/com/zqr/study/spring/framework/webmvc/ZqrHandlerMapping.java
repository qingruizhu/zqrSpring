package com.zqr.study.spring.framework.webmvc;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-22 10:13
 */
public class ZqrHandlerMapping {

    //url匹配
    private Pattern pattern;
    //对应的方法
    private Method method;
    //对应的controller对象
    private Object controller;

    public ZqrHandlerMapping(Pattern pattern, Method method, Object controller) {
        this.pattern = pattern;
        this.method = method;
        this.controller = controller;
    }

    public Pattern getPattern() {
        return pattern;
    }

    public Method getMethod() {
        return method;
    }

    public Object getController() {
        return controller;
    }
}
