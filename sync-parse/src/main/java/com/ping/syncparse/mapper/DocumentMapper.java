package com.ping.syncparse.mapper;


import com.ping.syncparse.entity.DocumentEntity;
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
public class DocumentMapper {

    @Value("${order}")
    private String order;
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<DocumentEntity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        if ("desc".equals(order)) {
            query.with(Sort.by(Sort.Order.desc("_id")));
        } else if ("asc".equals(order)) {
            query.with(Sort.by(Sort.Order.asc("_id")));
        }

        query.with(pageRequest);
        return mongoTemplate.find(query, DocumentEntity.class);
    }
}
