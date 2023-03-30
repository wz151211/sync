package com.ping.syncparse.sync.c9;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 虚开发票
 */
@Repository
public class DocumentFalseInvoiceMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentFalseInvoiceEntity entity) {
        mongoTemplate.insert(entity);
    }
}
