package com.ping.syncparse.mapper;


import com.ping.syncparse.entity.DocumentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;


import java.util.ArrayList;
import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Repository
public class DocumentMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public List<DocumentEntity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        query.limit(pageSize).skip((long) pageNum * pageSize);
        return mongoTemplate.find(query, DocumentEntity.class);
    }

    public List<DocumentEntity> findList(Criteria criteria) {
        Query query = new Query();
        if (criteria == null) {
            return new ArrayList<>();
        }
        query.addCriteria(criteria);
        return mongoTemplate.find(query, DocumentEntity.class);
    }
}