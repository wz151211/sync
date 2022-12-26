package com.ping.syncmysqltomongo.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MongoMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentEntity entity) {
        mongoTemplate.insert(entity);
    }

    public void insertAll(List<DocumentEntity> entity) {
        mongoTemplate.insertAll(entity);
    }
}
