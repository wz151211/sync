package com.ping.syncsearch.task;

import com.ping.syncsearch.service.QueryService;
import com.ping.syncsearch.utils.CauseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
@Async
@Slf4j
public class Task11 {
    @Autowired
    private QueryService queryService;
    //  Criteria criteria = Criteria.where("s6").is("01").and("s11").in(CauseUtils.getCauseList("9300"));//知识产权合同纠纷
    //  Criteria criteria = Criteria.where("s6").is("01").and("s11").in(CauseUtils.getCauseList("9363")); //知识产权权属、侵权纠纷
    //   Criteria criteria = Criteria.where("s6").is("01").and("s11").is("保险诈骗");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("fyTree").is("浙江省").and("s6").is("01").and("s9").is("刑事一审").and("qwContent").regex("电信网络诈骗");
    //Criteria criteria = Criteria.where("s8").is("刑事案件").and("s6").is("04").and("qwContent").regex("强制医疗");
    Criteria criteria = Criteria.where("s6").is("01").and("s9").in("民事一审", "0301").and("s11").in(CauseUtils.getCauseList("9177"));

    //  Criteria criteria = Criteria.where("ayTree").regex("9363").and("s6").is("01"); //知识产权权属、侵权纠纷
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
