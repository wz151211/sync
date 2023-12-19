package com.ping.syncsearch.temp;

import com.ping.syncsearch.entity.Document2020Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

/**
 * @Author: W.Z
 * @Date: 2023/10/12 15:51
 */
@Repository
@Slf4j
public class TempMapper {

    @Autowired
    private MongoTemplate mongoTemplate;


    public void findCount() {
        Query query = new Query();
        query.addCriteria(Criteria.where("s11").is("金融借款合同纠纷"));
        long count = mongoTemplate.count(query, "ws_2016");
        log.info("数量为={}", count);

    }

    public void findCount(String collectName, Criteria criteria) {
        Query query = new Query();
        query.addCriteria(criteria);
        long count = mongoTemplate.count(query, collectName);
        log.info("集合={},数量为={}", collectName, count);

    }
}
