package com.ping.syncparse.service.contract;

import com.ping.syncparse.service.divorce.DivorceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ContractMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(ContractVo entity) {
        mongoTemplate.save(entity);
    }

    public void delete(ContractVo entity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(entity.getId()));
        mongoTemplate.remove(query, ContractVo.class);
    }

    public List<ContractVo> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, ContractVo.class);
    }

    public List<ContractVo> find(String caseNO) {
        Query query = new Query();
        Criteria criteria = Criteria.where("caseNo").is(caseNO);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, ContractVo.class);
    }
}
