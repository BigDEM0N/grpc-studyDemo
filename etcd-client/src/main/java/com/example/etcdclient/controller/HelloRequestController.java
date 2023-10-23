package com.example.etcdclient.controller;

import com.example.etcdclient.service.EtcdDiscoveryService;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.Response;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.example.HelloProto;
import org.example.HelloServiceGrpc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;

@RestController
public class HelloRequestController {

    @Autowired
    private EtcdDiscoveryService etcdDiscoveryService;

    private HelloServiceGrpc.HelloServiceBlockingStub helloServiceBlockingStub;

    @GetMapping("/hellorequest")
    public String helloRequest(){
        for(KeyValue kv : etcdDiscoveryService.getKvs()){
//            /**
//             * restful方式远程连接
//             * */
//            RestTemplate restTemplate = new RestTemplate();
//            try {
//                String url = kv.getValue().toString(StandardCharsets.UTF_8) + "/";
//                String response = restTemplate.getForObject(url, String.class);
//                System.out.println(response);
//                return response;
//            } catch (RestClientException e) {
//                System.err.println("Error accessing URL: " + e.getMessage());
//            }
            /**
             * grpc方式远程连接
             * */
            System.out.println(kv);
            ManagedChannel managedChannel = ManagedChannelBuilder.forTarget(kv.getValue().toString()).usePlaintext().build();
            try{
                HelloServiceGrpc.HelloServiceBlockingStub helloService = HelloServiceGrpc.newBlockingStub(managedChannel);
                System.out.println(helloService);
                HelloProto.HelloResponse response = helloService.hello(HelloProto.HelloRequest.newBuilder().setName("yong").build());
                String result = response.getResult();
                System.out.println(result);
                return result;
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                managedChannel.shutdown();
            }
        }
        return "error";
    }
}
