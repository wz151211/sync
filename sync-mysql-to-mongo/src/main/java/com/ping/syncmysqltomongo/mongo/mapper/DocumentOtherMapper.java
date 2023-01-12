package com.ping.syncmysqltomongo.mongo.mapper;


import com.ping.syncmysqltomongo.mongo.entity.DocumentOtherEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Repository
public class DocumentOtherMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentOtherEntity entity) {
        mongoTemplate.insert(entity);
    }

}
