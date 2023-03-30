package com.ping.syncparse.sync.c9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 偷税
 */
@Repository
public class DocumentEvasionMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentEvasionEntity entity) {
        mongoTemplate.insert(entity);
    }
}
