package com.ping.syncmongo.local;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CpwsMapper {
    @Autowired
    @Qualifier("localMongoTemplate")
    private MongoTemplate mongoTemplate;

    public void update(String id, String flag) {
        Query query = new Query(Criteria.where("_id").is(id));
        Update update = new Update().set("flag", flag);
        mongoTemplate.updateFirst(query, update, CpwsEntity.class);
    }

    public void remove(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, CpwsEntity.class);
    }

    public List<CpwsEntity> selectList(Integer limit, Integer skip) {
        Query query = new Query();
        query.limit(limit).skip(skip);
        query.addCriteria(Criteria.where("flag").ne("400"));
        return mongoTemplate.find(query, CpwsEntity.class);
    }
}
