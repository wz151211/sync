package com.ping.syncsearch.temp;

import com.ping.syncsearch.entity.Document2014Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.*;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2024/2/20 06:22
 */
@Repository
@Slf4j
public class TempDataMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(TempData entity) {
        mongoTemplate.insert(entity);
    }

    public List<TempData> findAll() {
        return mongoTemplate.findAll(TempData.class);
    }

    public void update(TempData data) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(data.getCode()));
        Update update = new Update();
        update.set("province", data.getProvince());
        update.set("city", data.getCity());
        update.set("county", data.getCounty());
        mongoTemplate.updateFirst(query, update, TempData.class);
    }
}
