package com.ping.syncparse.service.criminal;

import com.ping.syncparse.service.contract.ContractVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2023/12/19 21:41
 */
@Repository
public class CriminalMapper {

    @Autowired
    private MongoTemplate mongoTemplate;


    public void insert(CriminalVO entity) {
        mongoTemplate.save(entity);
    }

    public void delete(CriminalVO entity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(entity.getId()));
        mongoTemplate.remove(query, CriminalVO.class);
    }

    public List<CriminalVO> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        return mongoTemplate.find(query, CriminalVO.class);
    }
}
