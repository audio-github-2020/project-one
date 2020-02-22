package com.itheima.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/user")
public class UserController {

    @Autowired
    private Environment environment;

    @RequestMapping(value="/hello")
    public String hello(){
        System.out.println("My first Spring Boot !");
        //获取配置文件信息
        String url=environment.getProperty("url");

        return "Hello ! Spring Boot !"+url;
    }
}
