package com.zqr.study.spring.framework.webmvc;

import com.zqr.study.spring.framework.annotation.ZqrRequestParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-22 10:13
 */
public class ZqrHandlerAdapter {


    public ZqrModelAndView handler(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
        ZqrHandlerMapping handlerMapping = (ZqrHandlerMapping)handler;
        //1.方法的形参列表
        Map<String, Integer> paramMappings = new HashMap<String, Integer>();
        Method method = handlerMapping.getMethod();
        Annotation[][] paramAnnos = method.getParameterAnnotations();
        //设置带有ZqrRequestParameter注解的参数
        for (int i = 0; i < paramAnnos.length; i++) {
            for (Annotation annotation : paramAnnos[i]) {
                if (annotation instanceof ZqrRequestParameter) {
                    String paramName = ((ZqrRequestParameter) annotation).value();
                    if (!"".equals(paramName)){
                        paramMappings.put(paramName,i);
                    }
                }
            }
        }
        //设置req,resp参数
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clzz = parameterTypes[i];
            if (clzz == HttpServletRequest.class || clzz == HttpServletResponse.class) {
                paramMappings.put(clzz.getName(), i);
            }
        }
        //2.构建实参列表
        Object[] paramValues = new Object[parameterTypes.length];
        //页面传过来的参数都是string类型的
        Map<String,String[]> parameterMap = req.getParameterMap();
        Set<Map.Entry<String, String[]>> entries = parameterMap.entrySet();
        for (Map.Entry<String,String[]> entry:entries) {
             if (paramMappings.containsKey(entry.getKey())) {
                 String value = Arrays.toString(entry.getValue()).replaceAll("\\[|\\]", "").replaceAll("\\s", "");
                 Integer index = paramMappings.get(entry.getKey());
                 paramValues[index] = convertStringParam(value,parameterTypes[index]);
             }
        }
        //设置req,resp实参
        if (paramMappings.containsKey(HttpServletRequest.class.getName())) {
            Integer index = paramMappings.get(HttpServletRequest.class.getName());
            paramValues[index] = req;
        }
        if (paramMappings.containsKey(HttpServletResponse.class.getName())) {
            Integer index = paramMappings.get(HttpServletResponse.class.getName());
            paramValues[index] = resp;
        }

        Object result = method.invoke(handlerMapping.getController(), paramValues);
        if (null == result) return null;
        if (handlerMapping.getMethod().getReturnType() == ZqrModelAndView.class) {
            return (ZqrModelAndView) result;
        }
        return null;
    }

    private Object convertStringParam(String value, Class<?> parameterType) {
        if (parameterType == String.class) {
            return value;
        }else if (parameterType == Integer.class) {
            return Integer.valueOf(value);
        }else if (parameterType == int.class) {
            return Integer.valueOf(value).intValue();
        } else {
            return null;
        }
    }

    public boolean supports(Object handler) {
        return handler instanceof ZqrHandlerMapping;
    }
}
