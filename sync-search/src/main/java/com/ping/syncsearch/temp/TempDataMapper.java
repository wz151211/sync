package com.ping.syncsearch.temp;

import com.ping.syncsearch.entity.Document2014Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

/**
 * @Author: W.Z
 * @Date: 2024/2/20 06:22
 */
@Repository
@Slf4j
public class TempDataMapper {
    @Autowired
    private MongoTemplate mongoTemplate;

    public void insert(TempData entity) {
        mongoTemplate.insert(entity);
    }

}
