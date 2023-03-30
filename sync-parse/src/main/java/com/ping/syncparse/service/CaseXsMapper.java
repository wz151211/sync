package com.ping.syncparse.service;


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
public class CaseXsMapper {

    @Value("${order}")
    private String order;
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(CaseXsVo entity) {
        mongoTemplate.insert(entity);
    }

    public List<CaseXsVo> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        Sort sort = Sort.by(Sort.Direction.DESC,"refereeDate");
  /*      PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);*/
        query.with(sort);
        return mongoTemplate.find(query, CaseXsVo.class);
    }

}
