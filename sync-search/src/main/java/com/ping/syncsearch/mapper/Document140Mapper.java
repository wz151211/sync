package com.ping.syncsearch.mapper;

import com.ping.syncsearch.entity.Document140Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Document140Mapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document140Entity entity){
        mongoTemplate.insert(entity);
    }
}
