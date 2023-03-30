package com.ping.syncparse.sync.c140;

import com.ping.syncparse.service.FraudEntity;
import com.ping.syncparse.service.InternetFraudEntity;
import com.ping.syncparse.service.TelecomFraudEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class SyncTempService {
    @Autowired
    private MongoTemplate mongoTemplate;
    Criteria criteria = Criteria.where("caseType").is("民事案件").and("docType").is("判决书").and("trialProceedings").is("民事一审").and("htmlContent").regex("彩礼");
    private AtomicInteger pageNum = new AtomicInteger(-1);
    private int pageSize = 1000;

    public void sync() {
        pageNum.getAndIncrement();
        Query query = new Query();
        query.addCriteria(criteria);
        PageRequest pageRequest = PageRequest.of(pageNum.get(), pageSize);
        query.with(pageRequest);
        List<DocumentJtblEntity> entities = mongoTemplate.find(query, DocumentJtblEntity.class);
        for (DocumentJtblEntity entity : entities) {
            DocumentJBEntity jbEntity = new DocumentJBEntity();
            BeanUtils.copyProperties(entity, jbEntity);
            try {
                mongoTemplate.insert(jbEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AtomicInteger pageNumTelecomFraud = new AtomicInteger(-1);

    public void syncTelecomFraud() {
        pageNumTelecomFraud.getAndIncrement();
        Query query = new Query();
        query.addCriteria(Criteria.where("caseType").is("刑事案件").and("docType").is("判决书").and("htmlContent").regex("电信诈骗"));
        PageRequest pageRequest = PageRequest.of(pageNumTelecomFraud.get(), pageSize);
        query.with(pageRequest);
        List<FraudEntity> entities = mongoTemplate.find(query, FraudEntity.class);
        for (FraudEntity entity : entities) {
            TelecomFraudEntity jbEntity = new TelecomFraudEntity();
            BeanUtils.copyProperties(entity, jbEntity);
            try {
                mongoTemplate.insert(jbEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private AtomicInteger pageNumInternetFraudEntity = new AtomicInteger(-1);

    public void syncInternetFraudEntity() {
        pageNumInternetFraudEntity.getAndIncrement();
        Query query = new Query();
        query.addCriteria(Criteria.where("caseType").is("刑事案件").and("docType").is("判决书").and("htmlContent").regex("网络诈骗"));
        PageRequest pageRequest = PageRequest.of(pageNumInternetFraudEntity.get(), pageSize);
        query.with(pageRequest);
        List<FraudEntity> entities = mongoTemplate.find(query, FraudEntity.class);
        for (FraudEntity entity : entities) {
            InternetFraudEntity jbEntity = new InternetFraudEntity();
            BeanUtils.copyProperties(entity, jbEntity);
            try {
                mongoTemplate.insert(jbEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
