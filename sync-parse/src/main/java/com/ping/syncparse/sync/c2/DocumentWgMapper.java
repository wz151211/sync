package com.ping.syncparse.sync.c2;

import com.ping.syncparse.sync.c8.Document8Entity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DocumentWgMapper {

    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DocumentWgEntity entity){
        mongoTemplate.insert(entity);
    }
}
