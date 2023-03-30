package com.ping.syncparse.sync.c140;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentDq1Mapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentDq1Entity entity){
        mongoTemplate.insert(entity);
    }
}
