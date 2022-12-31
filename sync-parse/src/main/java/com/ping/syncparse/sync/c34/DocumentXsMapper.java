package com.ping.syncparse.sync.c34;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentXsMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentXsLhEntity entity){
        mongoTemplate.insert(entity);
    }
}
