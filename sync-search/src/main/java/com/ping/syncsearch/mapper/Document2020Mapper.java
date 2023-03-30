package com.ping.syncsearch.mapper;


import com.ping.syncsearch.entity.Document2020Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class Document2020Mapper {
    @Value("${order}")
    private String order;
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document2020Entity entity) {
        mongoTemplate.insert(entity);
    }

    public void delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Document2020Entity.class);
    }

    public List<Document2020Entity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, Document2020Entity.class);
    }

    public Long getCount(Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        return mongoTemplate.count(query, Document2020Entity.class);
    }
}
