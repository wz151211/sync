package com.ping.syncmysqltomongo.mongo.mapper;


import com.ping.syncmysqltomongo.mongo.entity.Document6Entity;
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
public class Document6Mapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document6Entity entity) {
        mongoTemplate.insert(entity);
    }

    public List<Document6Entity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, Document6Entity.class);
    }
}