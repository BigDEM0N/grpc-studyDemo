package com.example.etcdregister.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @CrossOrigin(origins = "http://localhost:8081")
    @GetMapping("/")
    public String hello(){
        System.out.println("访问成功");
        return "ok";
    }

}
