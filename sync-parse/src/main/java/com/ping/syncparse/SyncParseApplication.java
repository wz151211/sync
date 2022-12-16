package com.ping.syncparse;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncParseApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncParseApplication.class, args);
    }

}
