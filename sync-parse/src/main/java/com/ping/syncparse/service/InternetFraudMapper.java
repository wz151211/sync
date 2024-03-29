package com.ping.syncparse.service;

import com.ping.syncparse.service.export.ExportResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InternetFraudMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(InternetFraudEntity entity) {
        mongoTemplate.insert(entity);
    }

    public List<InternetFraudEntity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
    /*    PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);*/
        return mongoTemplate.find(query, InternetFraudEntity.class);
    }

    public List<DocumentTargetEntity> findtargetList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, DocumentTargetEntity.class);
    }

    public void delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, DocumentTargetEntity.class);
    }


}
