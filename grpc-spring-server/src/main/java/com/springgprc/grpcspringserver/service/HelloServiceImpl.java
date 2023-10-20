package com.springgprc.grpcspringserver.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.HelloProto;
import org.example.HelloServiceGrpc;

/**
 * 服务端接口的实现
 * */
@GrpcService
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void hello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        String name = request.getName();
        System.out.println("服务端 接收 "+name);

        responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("服务端接收成功 返回 "+name).build());
        responseObserver.onCompleted();
    }
}
