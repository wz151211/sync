package com.ping.syncparse.sync.c140;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DocumentDqMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentDqEntity entity) {
        mongoTemplate.insert(entity);
    }

    public void delete(DocumentDqEntity entity) {
        Query query = new Query();
        Criteria criteria = Criteria.where("_id").is(entity.getId());
        query.addCriteria(criteria);
        mongoTemplate.remove(query, DocumentDqEntity.class);
    }

    public List<DocumentDqEntity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, DocumentDqEntity.class);
    }
}
