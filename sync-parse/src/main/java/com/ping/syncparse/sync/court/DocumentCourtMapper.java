package com.ping.syncparse.sync.court;

import com.ping.syncparse.sync.c9.DocumentTaxEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * 税务行政管理(税务）
 */
@Repository
public class DocumentCourtMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentCourtEntity entity) {
        mongoTemplate.insert(entity);
    }
}
