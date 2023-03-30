package com.ping.syncparse.sync.c140;

import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class Document140Mapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document140Entity entity){
        mongoTemplate.insert(entity);
    }

    public List<Document140Entity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, Document140Entity.class);
    }
}
