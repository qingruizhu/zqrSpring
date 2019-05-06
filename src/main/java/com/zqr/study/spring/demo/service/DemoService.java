package com.zqr.study.spring.demo.service;


import com.zqr.study.spring.framework.annotation.ZqrService;

@ZqrService
public class DemoService  implements IDemoService {


    public String sayHello(String word) {
        System.out.println("你好啊.......");
        return word+" what are you doing ?";
    }
}
