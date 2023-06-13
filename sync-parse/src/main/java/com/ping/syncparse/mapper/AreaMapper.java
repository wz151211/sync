package com.ping.syncparse.mapper;

import com.ping.syncparse.entity.AreaEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

@Repository
public class AreaMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public AreaEntity find(String city, String county) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.isEmpty(city) && StringUtils.isEmpty(county)) {
            return null;
        }
        if (StringUtils.hasText(city) && StringUtils.hasText(county)) {
            criteria.and("city").is(city.trim()).orOperator(Criteria.where("county").in(city.trim(), county.trim()));
        } else if (StringUtils.hasText(city) && StringUtils.isEmpty(county)) {
            criteria.and("name").is(city.trim());
        } else if ((StringUtils.isEmpty(city) && StringUtils.hasLength(county))) {
            criteria.and("name").is(county.trim());
        }
        if (criteria != null) {
            query.addCriteria(criteria);
        }
        List<AreaEntity> entities = mongoTemplate.find(query, AreaEntity.class);
        if (entities != null && entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }
}
