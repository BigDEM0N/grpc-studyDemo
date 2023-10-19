package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

public class GrpcClient1 {
    public static void main(String[] args) {
        //1.创建通信管道
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        try{
            //2.获取stub 代理对象
            HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
            //3.完成RPC调用
            //准备参数
            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("yong");
            HelloProto.HelloRequest helloRequest = builder.build();
            //rpc调用，获取response
            HelloProto.HelloResponse helloResponse = helloService.hello(helloRequest);
            String result = helloResponse.getResult();
            System.out.println(result);
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            managedChannel.shutdown();
        }
    }
}
