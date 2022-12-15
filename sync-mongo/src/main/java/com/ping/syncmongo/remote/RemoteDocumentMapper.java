package com.ping.syncmongo.remote;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Repository
public class RemoteDocumentMapper {

    @Autowired
    @Qualifier("remoteMongoTemplate")
    private MongoTemplate mongoTemplate;

    public void save(DocumentEntity entity) {
        mongoTemplate.insert(entity);
    }

    public void insertAll(List<DocumentEntity> entities) {
        mongoTemplate.insertAll(entities);
    }
}
