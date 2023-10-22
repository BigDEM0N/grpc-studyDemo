package com.example.etcdregister;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class EtcdRegisterApplication {

    public static void main(String[] args) {
        SpringApplication.run(EtcdRegisterApplication.class, args);
    }

}
