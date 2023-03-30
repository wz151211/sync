package com.ping.syncparse.sync.c9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 危害税收征管罪
 */
@Repository
public class DocumentRevenueMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentRevenueEntity entity) {
        mongoTemplate.insert(entity);
    }
}
