package com.ping.syncmongo.remote;

import com.mongodb.client.result.DeleteResult;
import com.ping.syncmongo.local.entity.Document2Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Repository
@Slf4j
public class RemoteDocumentMapper {

    @Value("${order}")
    private String order;
    @Autowired
    @Qualifier("remoteMongoTemplate")
    private MongoTemplate mongoTemplate;

    public void save(DocumentEntity entity) {
        mongoTemplate.insert(entity);
    }

    public void delete(List<String> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").in(ids));
        mongoTemplate.remove(query, DocumentEntity.class);

    }


    public List<DocumentEntity> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        if ("desc".equals(order)) {
            pageRequest.withSort(Sort.by(Sort.Order.desc("id")));
        } else if ("asc".equals(order)) {
            pageRequest.withSort(Sort.by(Sort.Order.asc("id")));
        }
        query.with(pageRequest);
        return mongoTemplate.find(query, DocumentEntity.class);
    }
}
