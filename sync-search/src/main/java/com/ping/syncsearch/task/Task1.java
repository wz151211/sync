package com.ping.syncsearch.task;

import com.ping.syncsearch.service.QueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Async
@Slf4j
public class Task1 {
    @Autowired
    private QueryService queryService;

    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("刑讯逼供");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("虐待被监管人");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("滥用职权");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("玩忽职守");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("执行判决、裁定失职");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("执行判决、裁定滥用职权");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("私放在押人员");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("失职致使在押人员脱逃");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("拐骗儿童");
    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s9").in("民事一审", "0301").and("s6").is("01").and("s11").is("经济补偿金纠纷");

    //   Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("非国家工作人员受贿").and("s31").gte("2014-01-01");
    //  Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("职务侵占").and("s31").gte("2014-01-01");
    Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("挪用资金").and("s31").gte("2014-01-01");

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
