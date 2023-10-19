package org.example;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.Iterator;

public class GrpcClient2 {
    public static void main(String[] args) {
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress("localhost",9000).usePlaintext().build();

        try{
            HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);

            HelloProto.HelloRequest.Builder builder = HelloProto.HelloRequest.newBuilder();
            builder.setName("c2ss");
            HelloProto.HelloRequest helloRequest = builder.build();

            Iterator<HelloProto.HelloResponse> helloResponseIterator = helloService.c2ss(helloRequest);
            while(helloResponseIterator.hasNext()){
                HelloProto.HelloResponse helloResponse = helloResponseIterator.next();
                System.out.println(helloResponse);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            System.out.println("finished");
            managedChannel.shutdown();
        }
    }

}
