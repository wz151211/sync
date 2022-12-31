package com.ping.syncparse.sync.c140;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentQjMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentQjEntity entity){
        mongoTemplate.insert(entity);
    }
}
