package com.ping.syncparse.sync;

import com.ping.syncparse.entity.Document1Entity;
import com.ping.syncparse.entity.DocumentEntity;
import com.ping.syncparse.mapper.Document1Mapper;
import com.ping.syncparse.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class Sync140Service {

    @Autowired
    private DocumentMapper documentMapper;


    @Autowired
    private Document1Mapper document1Mapper;

    @Autowired
    private Document140Mapper document140Mapper;
    private AtomicInteger pageNum1 = new AtomicInteger(0);
    private AtomicInteger pageNum2 = new AtomicInteger(0);
    private final int pageSize = 1000;

    public void sync1() {
        Criteria criteria = Criteria
                .where("caseType").is("刑事案件");
        criteria.and("docType").is("判决书");
        criteria.orOperator(Criteria.where("name").regex("盗窃"), Criteria.where("name").regex("抢劫"));
        log.info("pageNum={}", pageNum1.get());
        List<DocumentEntity> list = documentMapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
        for (DocumentEntity entity : list) {
            Document140Entity document140Entity = new Document140Entity();
            BeanUtils.copyProperties(entity, document140Entity);
            document140Mapper.insert(document140Entity);
        }
    }

    public void sync2() {
        Criteria criteria = Criteria
                .where("caseType").is("刑事案件");
        criteria.and("docType").is("判决书");
        criteria.orOperator(Criteria.where("name").regex("盗窃"), Criteria.where("name").regex("抢劫"));
        log.info("pageNum={}", pageNum2.get());
        List<Document1Entity> list = document1Mapper.findList(pageNum2.get(), pageSize, criteria);
        pageNum2.getAndIncrement();
        log.info("size={}", list.size());
        for (Document1Entity entity : list) {
            Document140Entity document140Entity = new Document140Entity();
            BeanUtils.copyProperties(entity, document140Entity);
            document140Mapper.insert(document140Entity);
        }
    }
}
