package com.ping.syncparse.sync.c140;

import cn.hutool.core.date.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class Sync140Service {

    @Autowired
    private Document140Mapper document140Mapper;
    @Autowired
    private DocumentDqMapper documentDqMapper;
    @Autowired
    private DocumentQjMapper qjMapper;

    @Autowired
    private DocumentDq1Mapper dq1Mapper;
    @Autowired
    private DocumentDq2Mapper dq2Mapper;
    @Autowired
    private DocumentDq3Mapper dq3Mapper;

    @Autowired
    private DocumentQjNewMapper qjNewMapper;
    private AtomicInteger pageNum = new AtomicInteger(-1);
    private final int pageSize = 10000;

    public void syncdq1() {
        if (pageNum.get() > 20) {
            pageNum.set(0);
        }
        pageNum.getAndIncrement();
        List<DocumentDqEntity> entities = documentDqMapper.findList(pageNum.get(), pageSize, null);
        for (DocumentDqEntity entity : entities) {
            DocumentDq1Entity dqEntity = new DocumentDq1Entity();
            if (entity.getJsonContent() != null) {
                toEntity(entity, dqEntity);
                try {
                    dq1Mapper.insert(dqEntity);
                    documentDqMapper.delete(entity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void syncdq2() {
        if (pageNum.get() > 20) {
            pageNum.set(0);
        }
        pageNum.getAndIncrement();
        List<DocumentDqEntity> entities = documentDqMapper.findList(pageNum.get(), pageSize, null);
        for (DocumentDqEntity entity : entities) {
            DocumentDq2Entity dqEntity = new DocumentDq2Entity();
            if (entity.getJsonContent() != null) {
                toEntity(entity, dqEntity);
                try {
                    dq2Mapper.insert(dqEntity);
                    documentDqMapper.delete(entity);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    public void syncdq3() {
        if (pageNum.get() > 20) {
            pageNum.set(0);
        }
        pageNum.getAndIncrement();
        List<DocumentDqEntity> entities = documentDqMapper.findList(pageNum.get(), pageSize, null);
        for (DocumentDqEntity entity : entities) {
            DocumentDq3Entity dqEntity = new DocumentDq3Entity();
            if (entity.getJsonContent() != null) {
                toEntity(entity, dqEntity);
                try {
                    dq3Mapper.insert(dqEntity);
                    documentDqMapper.delete(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }


    public void syncdq4() {
        pageNum.getAndIncrement();
        if (pageNum.get() > 20) {
            pageNum.set(0);
        }
        log.info("pageNum={}", pageNum.get());
        Date start = DateUtil.parse("2014-01-01").toJdkDate();
        Date end = DateUtil.parse("2014-12-31").toJdkDate();
        Criteria criteria = Criteria
                .where("refereeDate").gte(start).lte(end);
        List<DocumentDqEntity> entities = documentDqMapper.findList(pageNum.get(), pageSize, criteria);
        for (DocumentDqEntity entity : entities) {
            DocumentDq3Entity dqEntity = new DocumentDq3Entity();
            if (entity.getJsonContent() != null) {
          /*      long count = dq3Mapper.getCount(entity.getId());
                if (count > 0) {
                    continue;
                }*/
                toEntity(entity, dqEntity);
                try {
                    dq3Mapper.insert(dqEntity);
                    //   documentDqMapper.delete(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private Criteria criteria = Criteria
            .where("caseType").is("刑事案件")
            .and("docType").is("判决书")
            .and("name").regex("抢劫");

    /*    private Criteria criteria = Criteria
                .where("name").regex("抢劫");*/
    public void syncqj() {
        pageNum.getAndIncrement();
        List<DocumentQjEntity> entities = qjMapper.findList(pageNum.get(), pageSize, criteria);
        for (DocumentQjEntity entity : entities) {
            DocumentQjNewEntity dqEntity = new DocumentQjNewEntity();
            if (entity.getJsonContent() != null) {
                toEntity(entity, dqEntity);
                try {
                    qjNewMapper.insert(dqEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

    }

    private void toEntity(BaseEntity from, BaseEntity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
        if (from.getRefereeDate() != null) {
            to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), 8));
        }
        to.setCaseType(from.getCaseType());
        to.setCause(from.getCause());
        to.setParty(from.getParty());
        to.setKeyword(from.getKeyword());
        to.setTrialProceedings(from.getTrialProceedings());
        to.setDocType(from.getDocType());
        to.setHtmlContent(from.getHtmlContent());
        to.setJsonContent(from.getJsonContent());
        to.setCreateTime(new Date());
    }


    private void toEntity(DocumentQjEntity from, DocumentQjNewEntity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
        if (from.getRefereeDate() != null) {
            to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), 8));
        }
        to.setCaseType(from.getCaseType());
        to.setCause(from.getCause());
        to.setParty(from.getParty());
        to.setKeyword(from.getKeyword());
        to.setTrialProceedings(from.getTrialProceedings());
        to.setDocType(from.getDocType());
        to.setHtmlContent(from.getHtmlContent());
        to.setJsonContent(from.getJsonContent());
        to.setCreateTime(new Date());
    }

    private void toEntity(Document140Entity from, DocumentDqEntity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
        if (from.getRefereeDate() != null) {
            to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), 8));
        }
        to.setCaseType(from.getCaseType());
        to.setCause(from.getCause());
        to.setParty(from.getParty());
        to.setKeyword(from.getKeyword());
        to.setTrialProceedings(from.getTrialProceedings());
        to.setDocType(from.getDocType());
        to.setHtmlContent(from.getHtmlContent());
        to.setJsonContent(from.getJsonContent());
        to.setCreateTime(new Date());
    }
}
