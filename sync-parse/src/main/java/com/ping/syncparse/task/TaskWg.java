package com.ping.syncparse.task;

import com.ping.syncparse.sync.c2.SyncWgService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.atomic.AtomicInteger;


@Slf4j
public class TaskWg {
    @Autowired
    private SyncWgService searchService;
    private Criteria criteria = Criteria.where("htmlContent").regex("文化大革命");
    private AtomicInteger pageNum1 = new AtomicInteger(0);
    private AtomicInteger pageNum2 = new AtomicInteger(0);
    private AtomicInteger pageNum3 = new AtomicInteger(0);
    private AtomicInteger pageNum4 = new AtomicInteger(0);
    private AtomicInteger pageNum5 = new AtomicInteger(0);
    private AtomicInteger pageNum6 = new AtomicInteger(0);
    private AtomicInteger pageNum7 = new AtomicInteger(0);

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        try {
            searchService.sync1();
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2() {
        try {
            searchService.sync2();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save3() {
        try {
            searchService.sync3();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save4() {
        try {
            searchService.sync4();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save5() {
        try {
            searchService.sync5();
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Scheduled(initialDelay = 12 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save6() {
        try {
            searchService.sync6();
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Scheduled(initialDelay = 14 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save7() {
        try {
            searchService.sync7();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
