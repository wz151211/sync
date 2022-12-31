package com.ping.syncparse.sync.c34;

import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SyncMsService {
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
    private DocumentMsMapper documentMsMapper;

    private AtomicInteger pageNum1 = new AtomicInteger(-1);
    private AtomicInteger pageNum2 = new AtomicInteger(-1);
    private AtomicInteger pageNum3 = new AtomicInteger(-1);
    private AtomicInteger pageNum4 = new AtomicInteger(-1);
    private AtomicInteger pageNum5 = new AtomicInteger(-1);
    private AtomicInteger pageNum6 = new AtomicInteger(-1);
    private AtomicInteger pageNum7 = new AtomicInteger(-1);

    private final int pageSize = 100;

    private Criteria criteria = Criteria
            .where("caseType").is("民事案件")
            .and("docType").is("判决书")
            .orOperator(Criteria.where("htmlContent").regex("家庭暴力"));

    public void sync1() {
        pageNum1.getAndIncrement();
        log.info("pageNum1={}", pageNum1.get());
        List<Document1Entity> list = document1Mapper.findList(pageNum1.get(), pageSize, criteria);
        log.info("size={}", list.size());
        for (Document1Entity entity : list) {
            DocumentMsJtblEntity msEntity = new DocumentMsJtblEntity();
            BeanUtils.copyProperties(entity, msEntity);
            documentMsMapper.insert(msEntity);
        }
    }

    public void sync2() {
        pageNum2.getAndIncrement();
        log.info("pageNum2={}", pageNum2.get());
        List<Document2Entity> list = document2Mapper.findList(pageNum2.get(), pageSize, criteria);
        log.info("size={}", list.size());
        for (Document2Entity entity : list) {
            DocumentMsJtblEntity msEntity = new DocumentMsJtblEntity();
            BeanUtils.copyProperties(entity, msEntity);
            documentMsMapper.insert(msEntity);
        }
    }

    public void sync3() {
        pageNum3.getAndIncrement();
        log.info("pageNum3={}", pageNum3.get());
        List<Document3Entity> list = document3Mapper.findList(pageNum3.get(), pageSize, criteria);
        log.info("size={}", list.size());
        for (Document3Entity entity : list) {
            DocumentMsJtblEntity msEntity = new DocumentMsJtblEntity();
            BeanUtils.copyProperties(entity, msEntity);
            documentMsMapper.insert(msEntity);
        }
    }

    public void sync4() {
        pageNum4.getAndIncrement();
        log.info("pageNum4={}", pageNum4.get());
        List<Document4Entity> list = document4Mapper.findList(pageNum4.get(), pageSize, criteria);
        log.info("size={}", list.size());
        for (Document4Entity entity : list) {
            DocumentMsJtblEntity msEntity = new DocumentMsJtblEntity();
            BeanUtils.copyProperties(entity, msEntity);
            documentMsMapper.insert(msEntity);
        }
    }

    public void sync5() {
        pageNum5.getAndIncrement();
        log.info("pageNum5={}", pageNum5.get());
        List<Document5Entity> list = document5Mapper.findList(pageNum5.get(), pageSize, criteria);
        log.info("size={}", list.size());
        for (Document5Entity entity : list) {
            DocumentMsJtblEntity msEntity = new DocumentMsJtblEntity();
            BeanUtils.copyProperties(entity, msEntity);
            documentMsMapper.insert(msEntity);
        }
    }

    public void sync6() {
        pageNum6.getAndIncrement();
        log.info("pageNum6={}", pageNum6.get());
        List<Document6Entity> list = document6Mapper.findList(pageNum6.get(), pageSize, criteria);
        log.info("size={}", list.size());
        for (Document6Entity entity : list) {
            DocumentMsJtblEntity msEntity = new DocumentMsJtblEntity();
            BeanUtils.copyProperties(entity, msEntity);
            documentMsMapper.insert(msEntity);
        }
    }

    public void sync7() {
        pageNum7.getAndIncrement();
        log.info("pageNum7={}", pageNum7.get());
        List<Document7Entity> list = document7Mapper.findList(pageNum7.get(), pageSize, criteria);
        log.info("size={}", list.size());
        for (Document7Entity entity : list) {
            DocumentMsJtblEntity msEntity = new DocumentMsJtblEntity();
            BeanUtils.copyProperties(entity, msEntity);
            documentMsMapper.insert(msEntity);
        }
    }
}