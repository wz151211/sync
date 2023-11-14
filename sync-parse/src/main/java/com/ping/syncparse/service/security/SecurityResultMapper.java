package com.ping.syncparse.service.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2023/10/13 13:22
 */
@Repository
public class SecurityResultMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(SecurityResultVo entity) {
        mongoTemplate.save(entity);
    }


    public List<SecurityResultVo> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, SecurityResultVo.class);
    }
}
