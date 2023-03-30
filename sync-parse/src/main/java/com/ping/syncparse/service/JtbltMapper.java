package com.ping.syncparse.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JtbltMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(JtblEntity entity) {
        mongoTemplate.insert(entity);
    }

}
