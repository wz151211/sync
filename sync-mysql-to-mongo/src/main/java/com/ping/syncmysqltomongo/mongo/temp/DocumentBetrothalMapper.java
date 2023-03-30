package com.ping.syncmysqltomongo.mongo.temp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


@Repository
public class DocumentBetrothalMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentBetrothalEntity entity) {
        mongoTemplate.insert(entity);
    }

    public long getCount(String caseNo, String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("caseNo").is(caseNo).and("name").is(name));
        return mongoTemplate.count(query, "document_jb");
    }

}
