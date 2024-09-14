package com.yun.forum.controller;

import com.yun.forum.common.AppResult;
import com.yun.forum.exception.ApplicationException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @RequestMapping("/hello")
    public String hello() {
        return "Hello!!!";
    }

    @RequestMapping("/helloByName")
    public String helloByName(String name) {
        return "Hello!!!" + name;
    }

    @RequestMapping("/exception")
    public AppResult testException() throws Exception {
        throw new Exception("今天是个好日子");
    }

    @RequestMapping("/appException")
    public AppResult testAppException() {
        throw new ApplicationException("今天是个坏日子");
    }
}
