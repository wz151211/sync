package com.ping.syncsearch.mapper;


import com.ping.syncsearch.entity.Document2015Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class Document2015Mapper {
    @Value("${order}")
    private String order;
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document2015Entity entity) {
        mongoTemplate.insert(entity);
    }

    public void delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Document2015Entity.class);
    }

    public List<Document2015Entity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        if ("desc".equals(order)) {
            pageRequest.withSort(Sort.by(Sort.Order.desc("id")));
        } else if ("asc".equals(order)) {
            pageRequest.withSort(Sort.by(Sort.Order.asc("id")));
        }
        query.with(pageRequest);
        return mongoTemplate.find(query, Document2015Entity.class);
    }
}
