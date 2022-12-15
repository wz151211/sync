package com.ping.syncmysqltomongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncMysqlToMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncMysqlToMongoApplication.class, args);
    }

}
