package com.ping.syncparse.sync.c34;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocumentMsMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentMsJtblEntity entity) {
        mongoTemplate.insert(entity);
    }

    public List<DocumentMsJtblEntity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, DocumentMsJtblEntity.class);
    }
}
