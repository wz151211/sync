package com.ping.syncparse.task;

import com.ping.syncparse.sync.SearchService;
import com.ping.syncparse.sync.c34.SyncMsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;

/*@Component
@Async*/
@Slf4j
public class TaskMs {
    @Autowired
    private SyncMsService searchService;

    private Criteria criteria = Criteria
            .where("caseType").is("刑事案件")
            .and("docType").is("判决书")
            .and("htmlContent").regex("家庭暴力");

    private AtomicInteger pageNum1 = new AtomicInteger(0);
    private AtomicInteger pageNum2 = new AtomicInteger(0);
    private AtomicInteger pageNum3 = new AtomicInteger(0);
    private AtomicInteger pageNum4 = new AtomicInteger(0);
    private AtomicInteger pageNum5 = new AtomicInteger(0);
    private AtomicInteger pageNum6 = new AtomicInteger(0);
    private AtomicInteger pageNum7 = new AtomicInteger(0);

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
