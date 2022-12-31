package com.ping.syncparse.sync.c140;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentDqMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentDqEntity entity){
        mongoTemplate.insert(entity);
    }
}
