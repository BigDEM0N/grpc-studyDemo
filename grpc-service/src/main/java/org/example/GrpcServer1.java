package org.example;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.example.service.HelloServiceImpl;

import java.io.IOException;

public class GrpcServer1{
    public static void main(String[] args) throws IOException, InterruptedException {
        //1.绑定端口
        ServerBuilder serverBuilder = ServerBuilder.forPort(9000);

        //2.发布服务
        serverBuilder.addService(new HelloServiceImpl());

        //3.创建服务对象
        Server server = serverBuilder.build();


        server.start();
        server.awaitTermination();
    }
}
