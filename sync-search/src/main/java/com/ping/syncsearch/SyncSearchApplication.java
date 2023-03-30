package com.ping.syncsearch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncSearchApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncSearchApplication.class, args);
    }

}
