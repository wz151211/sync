package com.ping.syncsearch.temp;

import com.ping.syncsearch.entity.Document2020Entity;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

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


    public List<TempVO> group(String collectName) {
        Aggregation agg = Aggregation.newAggregation(Aggregation.group("id").first("s3").as("code").count().as("count").first("fyTree").as("tree").first("s2").as("name"));
        return mongoTemplate.aggregate(agg, collectName, TempVO.class).getMappedResults();
        // result.getMappedResults().forEach(document -> System.out.println(document));
        // Document rawResults = result.getRawResults();
        // System.out.println(rawResults);

    }

    public List<TempVO> find(String year, String type) {
        Query query = new Query();
        Criteria criteria = Criteria.where("year").is(year).and("type").is(type);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, TempVO.class);
    }


}
