package com.ping.syncsearch.task;

import com.ping.syncsearch.service.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

//@Component
//@Async
@Slf4j
public class Task10 {
    @Autowired
    private QueryService queryService;

    Criteria criteria = Criteria.where("s11").in(
            "危害税收征管",
                    "逃税",
                    "偷税",
                    "抗税",
                    "逃避追缴欠税",
                    "骗取出口退税",
                    "虚开增值税专用发票、用于骗取出口退税、抵扣税款发票",
                    "虚开发票",
                    "伪造、出售伪造的增值税专用发票",
                    "非法出售增值税专用发票",
                    "非法购买增值税专用发票、购买伪造的增值税专用发票",
                    "非法制造、出售非法制造的用于骗取出口退税、抵扣税款发票",
                    "非法制造、出售非法制造的发票",
                    "非法出售用于骗取出口退税、抵扣税款发票",
                    "非法出售发票",
                    "持有伪造的发票");

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60)
    public void save2014() {
        try {
            queryService.sync2014(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60)
    public void sync2015() {
        try {
            queryService.sync2015(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60)
    public void save2016() {
        try {
            queryService.sync2016(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60)
    public void save2017() {
        try {
            queryService.sync2017(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60)
    public void save2018() {
        try {
            queryService.sync2018(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 12 * 1000L, fixedRate = 1000 * 60)
    public void save2019() {
        try {
            queryService.sync2019(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 14 * 1000L, fixedRate = 1000 * 60)
    public void save2020() {
        try {
            queryService.sync2020(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 16 * 1000L, fixedRate = 1000 * 60)
    public void save2021() {
        try {
            queryService.sync2021(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 18 * 1000L, fixedRate = 1000 * 60)
    public void save2022() {
        try {
            queryService.sync2022(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 20 * 1000L, fixedRate = 1000 * 60)
    public void save2023() {
        try {
            queryService.sync2023(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
