package com.ping.syncparse.mapper;


import com.ping.syncparse.entity.Document2015_2016Entity;
import com.ping.syncparse.entity.Document2017Entity;
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
public class Document2017Mapper {
    @Value("${order}")
    private String order;
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document2017Entity entity) {
        mongoTemplate.insert(entity);
    }

    public void delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, Document2017Entity.class);
    }
    public List<Document2017Entity> findList(int pageNum, int pageSize, Criteria criteria) {
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
        return mongoTemplate.find(query, Document2017Entity.class);
    }

    public Long getCount(Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        return mongoTemplate.count(query, Document2017Entity.class);
    }
}