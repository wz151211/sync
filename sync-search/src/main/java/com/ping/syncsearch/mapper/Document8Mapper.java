package com.ping.syncsearch.mapper;

import com.ping.syncsearch.entity.Document8Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Document8Mapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document8Entity entity){
        mongoTemplate.insert(entity);
    }
}
