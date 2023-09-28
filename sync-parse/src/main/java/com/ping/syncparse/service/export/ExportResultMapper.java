package com.ping.syncparse.service.export;

import com.ping.syncparse.service.CaseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExportResultMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public List<ExportResultVO> findList(int pageNum, int pageSize, Criteria criteria) {
        Query query = new Query();
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "refereeDate");
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize);
        query.with(pageRequest);
        // query.with(sort);
        return mongoTemplate.find(query, ExportResultVO.class);
    }

    public void delete(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, ExportResultVO.class);
    }
}
