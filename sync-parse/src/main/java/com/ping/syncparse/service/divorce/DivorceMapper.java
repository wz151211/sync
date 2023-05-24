package com.ping.syncparse.service.divorce;

import com.ping.syncparse.service.CaseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public class DivorceMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(DivorceVo entity) {
        mongoTemplate.save(entity);
    }

    public List<DivorceVo> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, DivorceVo.class);
    }

    public List<DivorceVo> find(String caseNO) {
        Query query = new Query();
        Criteria criteria = Criteria.where("caseNo").is(caseNO);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, DivorceVo.class);
    }
}
