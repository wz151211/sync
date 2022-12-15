package com.ping.syncmysqlmongo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncMysqlMongoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SyncMysqlMongoApplication.class, args);
    }

}
