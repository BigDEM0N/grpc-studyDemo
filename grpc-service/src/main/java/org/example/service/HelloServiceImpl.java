package org.example.service;

import io.grpc.stub.StreamObserver;
import org.example.HelloProto;
import org.example.HelloServiceGrpc;

public class HelloServiceImpl extends HelloServiceGrpc.HelloServiceImplBase {
    @Override
    public void c2ss(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        String name = request.getName();
        System.out.println(name);
        //流式响应
        for(int i = 0;i<10;i++){
            HelloProto.HelloResponse.Builder builder = HelloProto.HelloResponse.newBuilder();
            builder.setResult(""+i);
            HelloProto.HelloResponse helloResponse = builder.build();
            responseObserver.onNext(helloResponse);

            try {
                Thread.sleep(1000);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        responseObserver.onCompleted();
    }

    /**
     * 1. 接受client提交的参数
     * 2. 业务处理service+dao
     * 3. 提供返回值
    */

    @Override
    public void hello(HelloProto.HelloRequest request, StreamObserver<HelloProto.HelloResponse> responseObserver) {
        //获取提交的参数
        String name = request.getName();
        //业务处理
        System.out.println("this is request" + name);
        //提供返回值 封装响应
        // 1.创建响应对象的构造者
        // 2.填充数据
        // 3.封装响应
        HelloProto.HelloResponse.Builder builder = HelloProto.HelloResponse.newBuilder();
        builder.setResult("hello method invoke ok " + name);
        HelloProto.HelloResponse helloResponse = builder.build();

        responseObserver.onNext(helloResponse);
        responseObserver.onCompleted();

    }
}
