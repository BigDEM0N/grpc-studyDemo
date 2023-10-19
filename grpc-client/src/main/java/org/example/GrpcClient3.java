package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *监听异步方式
*/
public class GrpcClient3 {
    public static void main(String[] args) throws InterruptedException {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();
        try{
            HelloServiceGrpc.HelloServiceStub helloService = HelloServiceGrpc.newStub(managedChannel);

            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("client3");
            HelloProto.HelloRequest helloRequest = builder.build();

            List<HelloProto.HelloResponse> list = new ArrayList<>();
            helloService.c2ss(helloRequest, new StreamObserver<HelloProto.HelloResponse>() {
                @Override
                public void onNext(HelloProto.HelloResponse helloResponse) {
                    System.out.println(helloResponse.getResult());
                    list.add(helloResponse);
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {
                    System.out.println(list);
                    System.out.println("end");
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            managedChannel.shutdown();
        }
        managedChannel.awaitTermination(30,TimeUnit.SECONDS);
    }
}
