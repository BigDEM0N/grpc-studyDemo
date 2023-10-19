package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class GrpcClient5 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        try{
            //创建代理
            HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);

            StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.cs2ss(new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    System.out.println("客户端 接收 "+helloResponse.getResult());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println("客户端接收结束");
                }
            });

            for(int i = 0;i<10;i++){
                helloRequestStreamObserver.onNext(HelloProto.HelloRequest.newBuilder().setName("" + i).build());
                Thread.sleep(1000);
            }

            helloRequestStreamObserver.onCompleted();
            managedChannel.awaitTermination(12, TimeUnit.SECONDS);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            managedChannel.shutdown();
        }
    }
}
