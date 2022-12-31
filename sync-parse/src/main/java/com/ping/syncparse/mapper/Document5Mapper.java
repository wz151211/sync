package com.ping.syncparse.mapper;


import com.ping.syncparse.entity.Document1Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Repository
public class Document1Mapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document1Entity entity) {
        mongoTemplate.insert(entity);
    }

}
