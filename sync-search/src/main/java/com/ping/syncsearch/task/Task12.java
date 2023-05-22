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
public class Task12 {
    @Autowired
    private QueryService queryService;

    // Criteria criteria = Criteria.where("s6").is("01").and("s11").in("组织、领导、参与恐怖组织","帮助恐怖活动","准备实施恐怖活动","宣扬恐怖主义、极端主义、煽动实施恐怖活动","利用极端主义破坏法律实施","非法持有宣扬恐怖主义、极端主义物品");
     Criteria criteria = Criteria.where("s6").is("01").and("s11").in("劫持船只、汽车","破坏广播电视设施、公用电信设施");
   // Criteria criteria = Criteria.where("s6").is("01").and("s11").in("非法制造、买卖、运输、邮寄、储存枪支、弹药、爆炸物", "非法制造、买卖、运输、储存危险物质", "违规制造、销售枪支", "盗窃、抢夺枪支、弹药、爆炸物", "盗窃、抢夺枪支、弹药、爆炸物、危险物质", "抢劫枪支、弹药、爆炸物、危险物质", "非法持有、私藏枪支、弹药");

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
