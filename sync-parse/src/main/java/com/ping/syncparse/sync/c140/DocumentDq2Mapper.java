package com.ping.syncparse.sync.c140;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentDq2Mapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentDq2Entity entity){
        mongoTemplate.insert(entity);
    }
}
