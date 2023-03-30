package com.ping.syncparse.sync.c9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 财政行政管理（财政）
 */
@Repository
public class DocumentFinanceMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentFinanceEntity entity) {
        mongoTemplate.insert(entity);
    }
}
