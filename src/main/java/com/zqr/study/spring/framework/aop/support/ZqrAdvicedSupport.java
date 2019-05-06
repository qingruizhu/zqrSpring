package com.zqr.study.spring.framework.aop.support;

import com.zqr.study.spring.framework.aop.ZqrAopConfig;
import com.zqr.study.spring.framework.aop.aspect.ZqrAfterReturningAdvice;
import com.zqr.study.spring.framework.aop.aspect.ZqrAfterThrowingAdvice;
import com.zqr.study.spring.framework.aop.aspect.ZqrMethodBeforeAdvice;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Description:
 * @Auther: qingruizhu
 * @Date: 2019-04-30 16:14
 */
public class ZqrAdvicedSupport {

    private Class targetClass;
    private Object target;
    private Pattern pointCutPattern;

    private transient Map<Method, List<Object>> methodCache;
    private ZqrAopConfig config;


    public boolean pointCutMatch() {
        return pointCutPattern.matcher(this.targetClass.toString()).matches();
    }

    public ZqrAdvicedSupport(ZqrAopConfig config) {
        this.config = config;
    }

    public Class getTargetClass() {
        return targetClass;
    }

    public void setTargetClass(Class targetClass) {
        this.targetClass = targetClass;
        parse();
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    public Pattern getPointCutPattern() {
        return pointCutPattern;
    }

    public void setPointCutPattern(Pattern pointCutPattern) {
        this.pointCutPattern = pointCutPattern;
    }

    public List<Object> getInterceptorsAndDynamicInterceptionAdvice(Method method, Class<?> targetClass) throws Exception {
        List<Object> caches = methodCache.get(method);
        if (null == caches) {
            Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
            caches = methodCache.get(targetMethod);
            //存入缓存
            this.methodCache.put(targetMethod, caches);
        }
        return caches;
    }


    //织入增强的方法，设置methodCache
    private void parse() {
        //pointCut 表达式
        String pointCut = config.getPointCut()
                .replaceAll("\\.", "\\\\.")
                .replaceAll("\\\\.\\*", ".*")
                .replaceAll("\\(", "\\\\(")
                .replaceAll("\\)", "\\\\)");
        //切点class表达式匹配
        String pointCutForClass = pointCut.substring(0, pointCut.lastIndexOf("\\(") - 4);
        pointCutPattern = Pattern.compile("class " + pointCutForClass.substring(pointCutForClass.lastIndexOf(" ") + 1));
        //设置切点的通知方法=mehtodCache
        methodCache = new HashMap<Method, List<Object>>();
        try {
            Class<?> aspectClass = Class.forName(config.getAspectClass());
            //切面的所有方法
            Map<String, Method> aspectMethods = new HashMap<String, Method>();
            for (Method method : aspectClass.getMethods()) {
                aspectMethods.put(method.getName(), method);
            }
            //匹配目标类的有关方法
            Pattern pattern = Pattern.compile(pointCut);
            for (Method method : targetClass.getMethods()) {
                String methodStr = method.toString();
                if (methodStr.contains("throws")) {
                    methodStr = methodStr.substring(0, methodStr.indexOf("throws")).trim();
                }
                Matcher matcher = pattern.matcher(methodStr);
                if (matcher.matches()) {
                    List<Object> advices = new ArrayList<Object>();
                    if (null != config.getAspectBefore() && !"".equals(config.getAspectBefore())) {
                        ZqrMethodBeforeAdvice beforeAdvice = new ZqrMethodBeforeAdvice(aspectMethods.get(config.getAspectBefore()), aspectClass.newInstance());
                        advices.add(beforeAdvice);
                    }
                    if (null != config.getAspectAfter() && !"".equals(config.getAspectAfter())) {
                        ZqrAfterReturningAdvice afterAdvice = new ZqrAfterReturningAdvice(aspectMethods.get(config.getAspectAfter()), aspectClass.newInstance());
                        advices.add(afterAdvice);
                    }
                    if (null != config.getAspectAfterThrow() && !"".equals(config.getAspectAfterThrow())) {
                        ZqrAfterThrowingAdvice throwingAdvice = new ZqrAfterThrowingAdvice(aspectMethods.get(config.getAspectAfterThrow()), aspectClass.newInstance());
                        advices.add(throwingAdvice);
                    }
                    methodCache.put(method, advices);
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }


    }
}
