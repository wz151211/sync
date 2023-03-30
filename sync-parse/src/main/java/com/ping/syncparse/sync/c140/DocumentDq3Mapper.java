package com.ping.syncparse.sync.c140;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocumentDq3Mapper {

    @Autowired
    private MongoTemplate mongoTemplate;


    public void insert(DocumentDq3Entity entity) {
        mongoTemplate.insert(entity);
    }

    public long getCount(String caseNo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("jsonContent.s5").is(caseNo));
        return mongoTemplate.count(query, DocumentDq3Entity.class);
    }
}
