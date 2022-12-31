package com.ping.syncparse.sync.c34;

import com.ping.syncparse.sync.c140.Document140Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentMsMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(Document140Entity entity){
        mongoTemplate.insert(entity);
    }
}
