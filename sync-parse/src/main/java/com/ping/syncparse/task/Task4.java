package com.ping.syncparse.task;

import com.ping.syncparse.sync.c9.service.SearchService4;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*@Component
@Async*/
@Slf4j
public class Task4 {
    @Autowired
    private SearchService4 searchService;
    Criteria criteria = Criteria.where("htmlContent").regex("偷税");

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        try {
            searchService.sync2014(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2() {
        try {
            searchService.sync2015(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save3() {
        try {
            searchService.sync2017(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save4() {
        try {
            searchService.sync2018(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save5() {
        try {
            searchService.sync2019(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 12 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save6() {
        try {
            searchService.sync2020(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 14 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save7() {
        try {
            searchService.sync2022(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
