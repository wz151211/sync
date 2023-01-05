package com.ping.syncparse.sync.c8;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
import com.ping.syncparse.sync.c8.Document8Entity;
import com.ping.syncparse.sync.c8.Document8Mapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
public class Sync8Service {
    @Autowired
    private Document1Mapper document1Mapper;
    @Autowired
    private Document2Mapper document2Mapper;
    @Autowired
    private Document3Mapper document3Mapper;
    @Autowired
    private Document4Mapper document4Mapper;
    @Autowired
    private Document5Mapper document5Mapper;
    @Autowired
    private Document6Mapper document6Mapper;
    @Autowired
    private Document7Mapper document7Mapper;
    @Autowired
    private Document8Mapper document8Mapper;

    @Autowired
    private DocumentOtherMapper otherMapper;

    private AtomicInteger pageNum1 = new AtomicInteger(-1);
    private AtomicInteger pageNum2 = new AtomicInteger(-1);
    private AtomicInteger pageNum3 = new AtomicInteger(-1);
    private AtomicInteger pageNum4 = new AtomicInteger(-1);
    private AtomicInteger pageNum5 = new AtomicInteger(-1);
    private AtomicInteger pageNum6 = new AtomicInteger(-1);
    private AtomicInteger pageNum7 = new AtomicInteger(-1);
    private AtomicInteger pageNum8 = new AtomicInteger(-1);

    private final int pageSize = 1000;
    private Criteria criteria = Criteria
            .where("caseType").is("行政案件").and("htmlContent").regex("行政协议");

    public void sync1() {
        pageNum1.getAndIncrement();
        log.info("pageNum1={}", pageNum1.get());
        List<Document1Entity> list = document1Mapper.findList(pageNum1.get(), pageSize, criteria);
        log.info("size1={}", list.size());
        for (Document1Entity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void sync2() {
        pageNum2.getAndIncrement();
        log.info("pageNum2={}", pageNum2.get());
        List<Document2Entity> list = document2Mapper.findList(pageNum2.get(), pageSize, criteria);
        log.info("size2={}", list.size());
        for (Document2Entity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync3() {
        pageNum3.getAndIncrement();
        log.info("pageNum3={}", pageNum3.get());
        List<Document3Entity> list = document3Mapper.findList(pageNum3.get(), pageSize, criteria);
        log.info("size3={}", list.size());
        for (Document3Entity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync4() {
        pageNum4.getAndIncrement();
        log.info("pageNum=4{}", pageNum4.get());
        List<Document4Entity> list = document4Mapper.findList(pageNum4.get(), pageSize, criteria);
        log.info("size4={}", list.size());
        for (Document4Entity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync5() {
        pageNum5.getAndIncrement();
        log.info("pageNum5={}", pageNum5.get());
        List<Document5Entity> list = document5Mapper.findList(pageNum5.get(), pageSize, criteria);
        log.info("size5={}", list.size());
        for (Document5Entity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync6() {
        pageNum6.getAndIncrement();
        log.info("pageNum6={}", pageNum6.get());
        List<Document6Entity> list = document6Mapper.findList(pageNum6.get(), pageSize, criteria);
        log.info("size6={}", list.size());
        for (Document6Entity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync7() {
        pageNum7.getAndIncrement();
        log.info("pageNum7={}", pageNum7.get());
        List<Document7Entity> list = document7Mapper.findList(pageNum7.get(), pageSize, criteria);
        log.info("size7={}", list.size());
        for (Document7Entity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync8() {
        pageNum8.getAndIncrement();
        log.info("pageNum8={}", pageNum8.get());
        List<DocumentOtherEntity> list = otherMapper.findList(pageNum8.get(), pageSize, criteria);
        log.info("size8={}", list.size());
        for (DocumentOtherEntity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            toEntity(entity, document8Entity);
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void toEntity(BaseEntity from, Document8Entity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
        if (from.getRefereeDate() != null) {
            to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), 8));
        }        to.setCaseType(from.getCaseType());
        to.setTrialProceedings(from.getTrialProceedings());
        to.setDocType(from.getDocType());
        to.setHtmlContent(from.getHtmlContent());
        to.setJsonContent(from.getJsonContent());
        to.setCreateTime(new Date());
        JSONObject jsonContent = from.getJsonContent();
        if (jsonContent != null) {
            String courtConsidered = jsonContent.getString("s26");
            String judgmentResult = jsonContent.getString("s27");
            to.setCourtConsidered(courtConsidered);
            to.setJudgmentResult(judgmentResult);
            JSONArray causes = jsonContent.getJSONArray("s11");
            String cause = null;
            if (causes != null) {
                cause = causes.stream().map(Object::toString).collect(joining(","));
                to.setCause(cause);
            }
            JSONArray partys = jsonContent.getJSONArray("s17");
            String party = null;
            if (partys != null) {
                party = partys.stream().map(Object::toString).collect(joining(","));
                to.setParty(party);
            }
            JSONArray keywords = jsonContent.getJSONArray("s45");
            String keyword = null;
            if (keywords != null) {
                keyword = keywords.stream().map(Object::toString).collect(joining(","));
                to.setKeyword(keyword);
            }
        }
    }
}
