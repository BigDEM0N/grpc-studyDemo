package com.example.grpcspringclient1.controller;

import net.devh.boot.grpc.client.inject.GrpcClient;
import org.example.HelloProto;
import org.example.HelloServiceGrpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GrpcClient("grpc-server")
    private HelloServiceGrpc.HelloServiceBlockingStub helloService;


    @GetMapping("/test1")
    public String test1(String name){
        System.out.println("controller 发送请求 "+name);
        HelloProto.HelloResponse helloResponse = helloService.hello(HelloProto.HelloRequest.newBuilder().setName(name).build());
        return "controller 收到"+helloResponse.getResult();
    }
}
