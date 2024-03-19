package com.ping.syncsearch;

import org.ansj.library.DicLibrary;
import org.ansj.util.MyStaticValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncSearchApplication {

    public static void main(String[] args) {
        MyStaticValue.ENV.put(DicLibrary.DEFAULT, "library/default.dic");
        SpringApplication.run(SyncSearchApplication.class, args);
    }

}
