package com.example.etcdregister.service;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.CallStreamObserver;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

@Service
public class EtcdServiceRegistry {
    @Autowired
    private Client etcdClient;

    @Value("${service.name}")
    private String serviceName;

    @Value("${service.address}")
    private String serviceAddress;

    @Value("${service.port}")
    private int port;

    @PostConstruct
    @Async
    public void registerService() throws Exception {

        Lease leaseClient = etcdClient.getLeaseClient();
        LeaseGrantResponse leaseGrantResponse = leaseClient.grant(10).get();
        long leaseId = leaseGrantResponse.getID();
        //生成key值和value

        ByteSequence key = ByteSequence.from(serviceName, StandardCharsets.UTF_8);
        System.out.println(key);
        ByteSequence value = ByteSequence.from(serviceAddress,StandardCharsets.UTF_8);
        System.out.println(value);


        KV kvClient = etcdClient.getKVClient();
        kvClient.put(key,value, PutOption.builder().withLeaseId(leaseId).build()).thenAccept(putResponse -> {
            leaseClient.keepAlive(leaseId, new CallStreamObserver<LeaseKeepAliveResponse>() {
                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setOnReadyHandler(Runnable runnable) {

                }

                @Override
                public void disableAutoInboundFlowControl() {

                }

                @Override
                public void request(int i) {

                }

                @Override
                public void setMessageCompression(boolean b) {

                }

                @Override
                public void onNext(LeaseKeepAliveResponse leaseKeepAliveResponse) {
                }

                @Override
                public void onError(Throwable throwable) {

                }

                @Override
                public void onCompleted() {

                }
            });
        });

    }

    @PostConstruct
    public void publishService() throws IOException, InterruptedException {
        Server server = ServerBuilder.forPort(port).addService(new HelloServiceImpl()).build();
        server.start();
        server.awaitTermination();
    }

}
