package com.example.etcdregister.service;

import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;
import org.example.HelloProto;
import org.example.HelloServiceGrpc;

@GrpcService
public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void hello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        String name = request.getName();
        System.out.println("grpc访问 "+ name);
        responseObserver.onNext(HelloProto.HelloResponse.newBuilder().setResult("ok").build());
        responseObserver.onCompleted();
    }
}
