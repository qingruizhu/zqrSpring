package com.zqr.study.spring.demo.controller;


import com.zqr.study.spring.demo.service.IDemoService;
import com.zqr.study.spring.framework.annotation.ZqrAutowired;
import com.zqr.study.spring.framework.annotation.ZqrController;
import com.zqr.study.spring.framework.annotation.ZqrRequestMapping;
import com.zqr.study.spring.framework.annotation.ZqrRequestParameter;
import com.zqr.study.spring.framework.webmvc.ZqrModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;

@ZqrController
@ZqrRequestMapping("demo")
public class DemoController {

    @ZqrAutowired
    private IDemoService demoService;

    @ZqrRequestMapping("/say.json")
    public void sayHello(
            HttpServletRequest req,
            HttpServletResponse resp,
            @ZqrRequestParameter("name") String name,
            @ZqrRequestParameter("age") String age){
        String s = demoService.sayHello("age:"+age+",name:"+name);
        try {
            resp.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @ZqrRequestMapping("/test.html")
    public ZqrModelAndView test(
            HttpServletRequest req,
            HttpServletResponse resp,
            @ZqrRequestParameter("name") String name,
            @ZqrRequestParameter("age") String age){
        HashMap<String, Object> model = new HashMap<String, Object>();
        model.put("name",name);
        model.put("age", age);
        model.put("data","\"你说\":\"滚蛋\",\"我说\":\"不滚不滚就不滚\"");

        ZqrModelAndView zqrModelAndView = new ZqrModelAndView("test.html",model);
        return zqrModelAndView;
    }


}
