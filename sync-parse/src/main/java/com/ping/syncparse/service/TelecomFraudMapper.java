package com.ping.syncparse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class TelecomFraudMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(TelecomFraudEntity entity) {
        mongoTemplate.insert(entity);
    }


}
