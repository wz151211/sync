package com.ping.syncsearch.mapper;

import com.ping.syncsearch.entity.AreaEntity;
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

    public AreaEntity findCounty(String city, String county) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.isEmpty(city) && StringUtils.isEmpty(county)) {
            return null;
        }
        if (StringUtils.hasText(city) && StringUtils.hasText(county)) {
            criteria.and("city").is(city.trim()).and("county").is(county.trim());
        }
        query.addCriteria(criteria);
        List<AreaEntity> entities = mongoTemplate.find(query, AreaEntity.class);
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public AreaEntity findCity(String city) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.isEmpty(city)) {
            return null;
        }
        criteria.and("name").is(city.trim());
        query.addCriteria(criteria);
        List<AreaEntity> entities = mongoTemplate.find(query, AreaEntity.class);
        if (entities.size() > 0) {
            return entities.get(0);
        }
        return null;
    }

    public List<AreaEntity> findCityChild(String city) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.isEmpty(city)) {
            return null;
        }
        criteria.and("city").is(city.trim()).and("level").is(3);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, AreaEntity.class);
    }

    public AreaEntity findProvince(String province) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.isEmpty(province)) {
            return null;
        }
        criteria.and("name").is(province.trim());
        query.addCriteria(criteria);
        return mongoTemplate.findOne(query, AreaEntity.class);
    }

    public List<AreaEntity> findProvinceChild(String province) {
        Query query = new Query();
        Criteria criteria = new Criteria();
        if (StringUtils.isEmpty(province)) {
            return null;
        }
        criteria.and("province").is(province.trim()).and("level").is(3);
        query.addCriteria(criteria);
        return mongoTemplate.find(query, AreaEntity.class);
    }

}
