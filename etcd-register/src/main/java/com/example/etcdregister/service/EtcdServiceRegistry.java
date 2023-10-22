package com.example.etcdregister.service;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.Lease;
import io.etcd.jetcd.lease.LeaseGrantResponse;
import io.etcd.jetcd.lease.LeaseKeepAliveResponse;
import io.etcd.jetcd.options.PutOption;
import io.grpc.stub.CallStreamObserver;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


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

    private final CountDownLatch latch = new CountDownLatch(1);

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
                    System.out.println("续租完成");
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

    @Scheduled(fixedRate = 6000)
    public void log(){
        System.out.println("wait");
    }
}
