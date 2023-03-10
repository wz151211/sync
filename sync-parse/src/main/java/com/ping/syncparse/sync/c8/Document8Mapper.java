package com.ping.syncparse.sync.c8;

import com.ping.syncparse.sync.c8.Document8Entity;
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
