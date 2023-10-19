package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.TimeUnit;

public class GrpcClient4 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost" ,9000).usePlaintext().build();

        try {
            HelloServiceGrpc.HelloServiceStub helloService  = HelloServiceGrpc.newStub(managedChannel);
            StreamObserver<HelloProto.HelloRequest> helloRequestStreamObserver = helloService.cs2s(new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    System.out.println(helloResponse.getResult());
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println("cs2s onCompleted");
                }
            });

            for(int i = 0;i< 10;i++){
                helloRequestStreamObserver.onNext(HelloProto.HelloRequest.newBuilder().setName("client send "+i).build());
                Thread.sleep(1000);
            }
            helloRequestStreamObserver.onCompleted();
            managedChannel.awaitTermination(12 , TimeUnit.SECONDS);

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            managedChannel.shutdown();
        }
    }
}
