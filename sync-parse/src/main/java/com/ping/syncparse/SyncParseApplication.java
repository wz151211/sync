package com.ping.syncparse;

import org.ansj.library.DicLibrary;
import org.ansj.util.MyStaticValue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncParseApplication {

    public static void main(String[] args) {
        //分词调用前重新设定字典路径
        MyStaticValue.ENV.put(DicLibrary.DEFAULT, "library/default.dic");
        SpringApplication.run(SyncParseApplication.class, args);
    }

}
