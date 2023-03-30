package com.ping.syncparse.task;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.sync.c9.service.SearchService1;
import com.ping.syncparse.sync.court.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/*@Component
@Async*/
@Slf4j
public class TaskCourt {
    @Autowired
    private SearchService searchService;

    Criteria criteria = Criteria.where("caseType").is("民事案件").and("refereeDate").gte(DateUtil.parse("2018-01-01").setField(DateField.HOUR, 8).toJdkDate()).lte(DateUtil.parse("2022-12-31").setField(DateField.HOUR, 8).toJdkDate())
            .and("courtName").regex("南通经济技术开发区人民法院");

    // Criteria.where("courtName").regex("南通市中级人民法院")
    //@Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        try {
            searchService.sync2014(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }


    //  @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2() {
        try {
            searchService.sync2015(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    // @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
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
            searchService.get2018Count(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save5() {
        try {
            searchService.get2019Count(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 12 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save6() {
        try {
            searchService.get2020Count(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 14 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save7() {
        try {
            searchService.get2022Count(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
