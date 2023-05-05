package com.ping.syncsearch.task;

import com.ping.syncsearch.service.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*@Component
@Async*/
@Slf4j
public class Task5 {
    @Autowired
    private QueryService queryService;

    Criteria criteria = Criteria.where("s8").is("刑事案件").and("fyTree").is("吉林省");

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2014() {
        try {
            queryService.sync2014(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void sync2015() {
        try {
            queryService.sync2015(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2016() {
        try {
            queryService.sync2016(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2017() {
        try {
            queryService.sync2017(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2018() {
        try {
            queryService.sync2018(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 12 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2019() {
        try {
            queryService.sync2019(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 14 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2020() {
        try {
            queryService.sync2020(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 16 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2021() {
        try {
            queryService.sync2021(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 18 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2022() {
        try {
            queryService.sync2022(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 20 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2023() {
        try {
            queryService.sync2023(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
