package com.ping.syncmysqltomongo.mongo.temp;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Repository
public class DocumentRevenueMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentRevenueEntity entity) {
        mongoTemplate.insert(entity);
    }

    public List<DocumentRevenueEntity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, DocumentRevenueEntity.class);
    }

    public void save(DocumentRevenueEntity entity) {
        mongoTemplate.save(entity);
    }
}
