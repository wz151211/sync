package com.ping.syncsearch.task;

import com.ping.syncsearch.temp.TempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @Author: W.Z
 * @Date: 2023/11/30 21:33
 */
//@Component
//@Async
@Slf4j
public class TempTask {

    @Autowired
    private TempService tempService;

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 1L)
    public void save2014() {
        tempService.findCount("ws_2014");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 2L)
    public void save2015() {
        tempService.findCount("ws_2015");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 3L)
    public void save2016() {
        tempService.findCount("ws_2016");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 4L)
    public void save2017() {
        tempService.findCount("ws_2017");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 5L)
    public void save2018() {
        tempService.findCount("ws_2018");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 6L)
    public void save2019() {
        tempService.findCount("ws_2019");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 7L)
    public void save2020() {
        tempService.findCount("ws_2020");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 8L)
    public void save2021() {
        tempService.findCount("ws_2021");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 9L)
    public void save2022() {
        tempService.findCount("ws_2022");
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 600 * 10L)
    public void save2023() {
        tempService.findCount("ws_2023");
    }


}
