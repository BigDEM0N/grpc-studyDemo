package com.example.etcdclient.service;

import io.etcd.jetcd.ByteSequence;
import io.etcd.jetcd.Client;
import io.etcd.jetcd.KV;
import io.etcd.jetcd.KeyValue;
import io.etcd.jetcd.kv.GetResponse;
import io.etcd.jetcd.options.GetOption;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class EtcdDiscoveryService {

    //连接etcd
    @Autowired
    private Client etcdClient;

    private List<KeyValue> kvs;

    @PostConstruct
    public void etcdDiscovery() {

        //创建KV客户端
        KV kvClient = etcdClient.getKVClient();

        //查找服务端前缀
        ByteSequence keyPrefix = ByteSequence.from("t", StandardCharsets.UTF_8);
        System.out.println(keyPrefix);

        //通过前缀获取Response
        CompletableFuture<GetResponse> getResponseCompletableFuture = kvClient.get(keyPrefix, GetOption.builder().isPrefix(true).build());
        System.out.println(getResponseCompletableFuture);

        try {
            kvs = getResponseCompletableFuture.get().getKvs();
            for(KeyValue kv : kvs){
                System.out.println(kv.getValue());
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        etcdClient.close();
    }
    public List<KeyValue> getKvs() {
        return kvs;
    }
}
