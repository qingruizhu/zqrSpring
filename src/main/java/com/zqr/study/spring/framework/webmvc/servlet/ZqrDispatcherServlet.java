package com.zqr.study.spring.framework.webmvc.servlet;
import com.zqr.study.spring.framework.annotation.ZqrController;
import com.zqr.study.spring.framework.annotation.ZqrRequestMapping;
import com.zqr.study.spring.framework.annotation.ZqrRequestParameter;
import com.zqr.study.spring.framework.context.ZqrApplicationContext;
import com.zqr.study.spring.framework.webmvc.*;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sun.javafx.scene.control.skin.Utils.getResource;


public class ZqrDispatcherServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            doDispatch(req, resp);
        }catch (Exception e){
            e.fillInStackTrace();
            resp.getWriter().write("<font size='25' color='blue'>500"+
                    "Exception</font><br/>Details:<br/>" +
                    Arrays.toString(e.getStackTrace()).replaceAll("\\[|\\]","") .replaceAll("\\s","\r\n") +
                    " <font color='green'><i>Copyright@GupaoEDU</i></font>");

        }
    }


    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) throws Exception{
        //1.根据用户请求的url匹配handlerMapping
        ZqrHandlerMapping handler = getHandler(req);
        //2.获取handlerAdapter
        ZqrHandlerAdapter adapter = getHandlerAdapter(handler);
        //3.调用方法获取返回值
        ZqrModelAndView modelAndView = adapter.handler(req,resp,handler);
        //4.真正的输出结果
        processDispatchResult(req,resp,modelAndView);

    }

    private void processDispatchResult(HttpServletRequest req, HttpServletResponse resp, ZqrModelAndView modelAndView) throws Exception{
        if (null == modelAndView) {
            return;
        }
        if (this.viewResolvers.isEmpty())return;
        for (ZqrViewResolver viewResolver : viewResolvers) {
            ZqrView zqrView = viewResolver.resolveView(modelAndView.getViewName(), null);
            if (null != zqrView) {
                zqrView.render(modelAndView.getModel(),req,resp);
                return;
            }

        }
    }

    private ZqrHandlerAdapter getHandlerAdapter(ZqrHandlerMapping handler) {
        if (this.handlerAdapters.isEmpty()) return null;
        ZqrHandlerAdapter adapter = this.handlerAdapters.get(handler);
        if (null != adapter && adapter.supports(handler)) {
            return adapter;
        }
        return null;
    }

    private ZqrHandlerMapping getHandler(HttpServletRequest req) {
        if (this.handlerMappings.isEmpty()) {
            return null;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "").replaceAll("/+", "/");
        for (ZqrHandlerMapping handler:this.handlerMappings) {
            Matcher matcher = handler.getPattern().matcher(url);
            if (!matcher.matches()) continue;
            return handler;
        }
        return null;
    }

    private final String LOCATION = "contextConfigLocation";
    //核心设计
    //ZqrHandlerMapping 最核心的设计，也是最经典的
    //它牛 B 到直接干掉了 Struts、Webwork 等 MVC 框架
    private List<ZqrHandlerMapping> handlerMappings = new ArrayList<ZqrHandlerMapping>();
    private Map<ZqrHandlerMapping, ZqrHandlerAdapter> handlerAdapters = new HashMap<ZqrHandlerMapping, ZqrHandlerAdapter>();
    private List<ZqrViewResolver> viewResolvers = new ArrayList<ZqrViewResolver>();
    private ZqrApplicationContext context;


    @Override
    public void init(ServletConfig config) throws ServletException {
        //初始化IOC容器
        context = new ZqrApplicationContext(config.getInitParameter(LOCATION));
        //策略初始化
        initStrategies(context);

    }

    protected void initStrategies(ZqrApplicationContext context) {
        /**
         * 有九种策略
         * 针对每个用户请求，都会经过一些处理的策略之后，最终结果输出
         * 每种策略可以自定义干预，但是最终的结果都是一致的。
         */

        /*************** 九大组件 *******************/
        // 1.文件上传解析，如果请求类型是multipart将通过MutipartResolver进行文件上传解析
        initMutipartResolver(context);
        // 2.本地化解析
        initLocaleResolver(context);
        // 3.主题解析
        initThemeResolver(context);
        // 4.通过handlerMapping,将请求映射到处理器
        initHadlerMappings(context);
        // 5.通过HandlerAdapter进行多类型的参数动态匹配
        initHandlerAdapters(context);
        // 6.执行过程中异常，交给HandlerExceptionResolver处理
        initHandlerExceptionResolvers(context);
        // 7.直接解析请求 到 视图名
        initRequestToViewNameTranslator(context);
        // 8.通过viewResolver解析逻辑视图到具体视图实现
        initViewResolvers(context);
        // 9.flash映射管理器
        initFlashManager(context);







    }

    private void initFlashManager(ZqrApplicationContext context) {
    }

    private void initViewResolvers(ZqrApplicationContext context) {
        //在页面敲一个 http://localhost/first.html
        //解决页面名字和模板关联的问题
        String templateRoot = context.getConfig().getProperty("templateRoot");
        ClassLoader classLoader = this.getClass().getClassLoader();
        URL resource = classLoader.getResource(templateRoot);
        if (null == resource)return;
        String file = resource.getFile();
        String templateRootPath = classLoader.getResource(templateRoot).getFile();
        File templateRootDir = new File(templateRootPath);
        for (File template:templateRootDir.listFiles()) {
            this.viewResolvers.add(new ZqrViewResolver(templateRoot));
        }
    }

    private void initRequestToViewNameTranslator(ZqrApplicationContext context) {

    }

    private void initHandlerExceptionResolvers(ZqrApplicationContext context) {

    }

    private void initHandlerAdapters(ZqrApplicationContext context) {
        //在初始化阶段，我们能做的就是，将这些参数的名字或者类型按一定的顺序保存下来
        //因为后面用反射调用的时候，传的形参是一个数组
        //可以通过记录这些参数的位置 index,挨个从数组中填值，这样的话，就和参数的顺序无关了
        for (ZqrHandlerMapping handlerMapping:this.handlerMappings) {
            //每一个方法有一个参数列表，那么这里保存的是形参列表
            this.handlerAdapters.put(handlerMapping, new ZqrHandlerAdapter());
        }

    }

    /**
     * 4
     */
    private void initHadlerMappings(ZqrApplicationContext context) {
        //获取IOC中所有的beanName;
        String[] beanNames = context.getBeanDefinitionNames();
        //从IOC中获取bean,根据class的注解组装handlerMapping
        for (String beanName:beanNames) {
            Object controller = context.getBean(beanName);
            Class<?> clzz = controller.getClass();
            //只找controller中的method组装handlerMapping
            if (!clzz.isAnnotationPresent(ZqrController.class)) continue;
            String baseUrl = "";
            if (clzz.isAnnotationPresent(ZqrRequestMapping.class)) {
                ZqrRequestMapping requestMapping = clzz.getAnnotation(ZqrRequestMapping.class);
                baseUrl = requestMapping.value();
            }
            //匹配方法上的注解地址
            Method[] methods = clzz.getMethods();
            for (Method method : methods) {
                if (!method.isAnnotationPresent(ZqrRequestMapping.class)) continue;
                ZqrRequestMapping zqrRequestMapping = method.getAnnotation(ZqrRequestMapping.class);
                String url = ("/"+baseUrl+zqrRequestMapping.value().trim().replaceAll("\\*",".*")).replaceAll("/+","/");
                //创建HandlerMapping,添加
                Pattern pattern = Pattern.compile(url);
                this.handlerMappings.add(new ZqrHandlerMapping(pattern,method,controller));
                System.out.println("Mapping: "+url+""+method);
            }


        }

    }

    private void initThemeResolver(ZqrApplicationContext context) {

    }

    private void initLocaleResolver(ZqrApplicationContext context) {

    }

    private void initMutipartResolver(ZqrApplicationContext context) {

    }
}
