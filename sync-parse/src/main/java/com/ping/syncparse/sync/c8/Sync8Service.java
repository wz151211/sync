package com.ping.syncparse.sync;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
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

    private AtomicInteger pageNum1 = new AtomicInteger(0);
    private AtomicInteger pageNum2 = new AtomicInteger(0);
    private AtomicInteger pageNum3 = new AtomicInteger(0);
    private AtomicInteger pageNum4 = new AtomicInteger(0);
    private AtomicInteger pageNum5 = new AtomicInteger(0);
    private AtomicInteger pageNum6 = new AtomicInteger(0);
    private AtomicInteger pageNum7 = new AtomicInteger(0);

    private final int pageSize = 100;
    private Criteria criteria = Criteria
            .where("caseType").is("行政案件").and("htmlContent").regex("行政协议");

    public void sync1() {
        log.info("pageNum={}", pageNum1.get());
        List<Document1Entity> list = document1Mapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
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
        log.info("pageNum={}", pageNum1.get());
        List<Document2Entity> list = document2Mapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
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
        log.info("pageNum={}", pageNum1.get());
        List<Document3Entity> list = document3Mapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
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
        log.info("pageNum={}", pageNum1.get());
        List<Document4Entity> list = document4Mapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
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
        log.info("pageNum={}", pageNum1.get());
        List<Document5Entity> list = document5Mapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
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
        log.info("pageNum={}", pageNum1.get());
        List<Document6Entity> list = document6Mapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
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
        log.info("pageNum={}", pageNum1.get());
        List<Document7Entity> list = document7Mapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
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

    private void toEntity(BaseEntity from, Document8Entity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
        to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), 8));
        to.setCaseType(from.getCaseType());
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
