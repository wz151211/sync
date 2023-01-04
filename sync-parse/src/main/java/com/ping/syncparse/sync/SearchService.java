package com.ping.syncparse.sync;

import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SearchService {
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
    private DocumentXsMapper documentXsMapper;

    private final int pageSize = 100;

    public void sync1(Criteria criteria, AtomicInteger pageNum) {
        pageNum.getAndIncrement();
        log.info("pageNum1={}", pageNum.get());
        List<Document1Entity> list = document1Mapper.findList(pageNum.get(), pageSize, criteria);
        log.info("size1={}", list.size());
        for (Document1Entity entity : list) {
            DocumentXsLhEntity xsEntity = new DocumentXsLhEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            documentXsMapper.insert(xsEntity);
        }
    }

    public void sync2(Criteria criteria, AtomicInteger pageNum) {
        pageNum.getAndIncrement();
        log.info("pageNum2={}", pageNum.get());
        List<Document2Entity> list = document2Mapper.findList(pageNum.get(), pageSize, criteria);
        log.info("size2={}", list.size());
        for (Document2Entity entity : list) {
            DocumentXsLhEntity xsEntity = new DocumentXsLhEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            documentXsMapper.insert(xsEntity);
        }
    }

    public void sync3(Criteria criteria, AtomicInteger pageNum) {
        pageNum.getAndIncrement();
        log.info("pageNum3={}", pageNum.get());
        List<Document3Entity> list = document3Mapper.findList(pageNum.get(), pageSize, criteria);
        log.info("size3={}", list.size());
        for (Document3Entity entity : list) {
            DocumentXsLhEntity xsEntity = new DocumentXsLhEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            documentXsMapper.insert(xsEntity);
        }
    }

    public void sync4(Criteria criteria, AtomicInteger pageNum) {
        pageNum.getAndIncrement();
        log.info("pageNum4={}", pageNum.get());
        List<Document4Entity> list = document4Mapper.findList(pageNum.get(), pageSize, criteria);
        log.info("size4={}", list.size());
        for (Document4Entity entity : list) {
            DocumentXsLhEntity xsEntity = new DocumentXsLhEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            documentXsMapper.insert(xsEntity);
        }
    }

    public void sync5(Criteria criteria, AtomicInteger pageNum) {
        pageNum.getAndIncrement();
        log.info("pageNum5={}", pageNum.get());
        List<Document5Entity> list = document5Mapper.findList(pageNum.get(), pageSize, criteria);
        log.info("size5={}", list.size());
        for (Document5Entity entity : list) {
            DocumentXsLhEntity xsEntity = new DocumentXsLhEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            documentXsMapper.insert(xsEntity);
        }
    }

    public void sync6(Criteria criteria, AtomicInteger pageNum) {
        pageNum.getAndIncrement();
        log.info("pageNum6={}", pageNum.get());
        List<Document6Entity> list = document6Mapper.findList(pageNum.get(), pageSize, criteria);
        log.info("size6={}", list.size());
        for (Document6Entity entity : list) {
            DocumentXsLhEntity xsEntity = new DocumentXsLhEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            documentXsMapper.insert(xsEntity);
        }
    }

    public void sync7(Criteria criteria, AtomicInteger pageNum) {
        pageNum.getAndIncrement();
        log.info("pageNum7={}", pageNum.get());
        List<Document7Entity> list = document7Mapper.findList(pageNum.get(), pageSize, criteria);
        log.info("size7={}", list.size());
        for (Document7Entity entity : list) {
            DocumentXsLhEntity xsEntity = new DocumentXsLhEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            documentXsMapper.insert(xsEntity);
        }
    }
}
