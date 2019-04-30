package com.zqr.study.spring.demo;


import com.zqr.study.spring.framework.annotation.ZqrAutowired;
import com.zqr.study.spring.framework.annotation.ZqrController;
import com.zqr.study.spring.framework.annotation.ZqrRequestMapping;
import com.zqr.study.spring.framework.annotation.ZqrRequestParameter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

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
            @ZqrRequestParameter("age") String age
    ){
        String s = demoService.sayHello("age:"+age+",name:"+name);
        try {
            resp.getWriter().write(s);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
