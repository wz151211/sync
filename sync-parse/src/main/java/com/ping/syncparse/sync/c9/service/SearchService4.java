package com.ping.syncparse.sync.c9.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.MD5;
import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
import com.ping.syncparse.sync.c9.DocumentEvasionEntity;
import com.ping.syncparse.sync.c9.DocumentEvasionMapper;
import com.ping.syncparse.sync.c9.DocumentFinanceEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SearchService4 {
    @Autowired
    private Document2014Mapper document2014Mapper;
    @Autowired
    private Document2015_2016Mapper document2015_2016Mapper;
    @Autowired
    private Document2017Mapper document2017Mapper;
    @Autowired
    private Document2018Mapper document2018Mapper;
    @Autowired
    private Document2019Mapper document2019Mapper;
    @Autowired
    private Document2020Mapper document2020Mapper;
    @Autowired
    private Document2021_2022Mapper document2021_2021Mapper;
    @Autowired
    private DocumentEvasionMapper documentEvasionMapper;

    private AtomicInteger pageNum2014 = new AtomicInteger(-1);
    private AtomicInteger pageNum2015 = new AtomicInteger(-1);
    private AtomicInteger pageNum2016 = new AtomicInteger(-1);
    private AtomicInteger pageNum2017 = new AtomicInteger(-1);
    private AtomicInteger pageNum2018 = new AtomicInteger(-1);
    private AtomicInteger pageNum2019 = new AtomicInteger(-1);
    private AtomicInteger pageNum2020 = new AtomicInteger(-1);
    private AtomicInteger pageNum2021 = new AtomicInteger(-1);
    private AtomicInteger pageNum2022 = new AtomicInteger(-1);

    private final int pageSize = 1000;

    public void sync2014(Criteria criteria) {
        pageNum2014.getAndIncrement();
        log.info("pageNum2014={}", pageNum2014.get());
        List<Document2014Entity> list = document2014Mapper.findList(pageNum2014.get(), pageSize, criteria);
        log.info("size2014={}", list.size());
        for (Document2014Entity entity : list) {
            convert(entity);
        }
    }

    public void sync2015(Criteria criteria) {
        pageNum2015.getAndIncrement();
        log.info("pageNum2015={}", pageNum2015.get());
        List<Document2015_2016Entity> list = document2015_2016Mapper.findList(pageNum2015.get(), pageSize, criteria);
        log.info("size2015={}", list.size());
        for (Document2015_2016Entity entity : list) {
            convert(entity);
        }
    }

    public void sync2017(Criteria criteria) {
        pageNum2017.getAndIncrement();
        log.info("pageNum2017={}", pageNum2017.get());
        List<Document2017Entity> list = document2017Mapper.findList(pageNum2017.get(), pageSize, criteria);
        log.info("size2017={}", list.size());
        for (Document2017Entity entity : list) {
            convert(entity);
        }
    }

    public void sync2018(Criteria criteria) {
        pageNum2018.getAndIncrement();
        log.info("pageNum2018={}", pageNum2018.get());
        List<Document2018Entity> list = document2018Mapper.findList(pageNum2018.get(), pageSize, criteria);
        log.info("size2018={}", list.size());
        for (Document2018Entity entity : list) {
            convert(entity);
        }
    }

    public void sync2019(Criteria criteria) {
        pageNum2019.getAndIncrement();
        log.info("pageNum2019={}", pageNum2019.get());
        List<Document2019Entity> list = document2019Mapper.findList(pageNum2019.get(), pageSize, criteria);
        log.info("size2019={}", list.size());
        for (Document2019Entity entity : list) {
            convert(entity);
        }
    }

    public void sync2020(Criteria criteria) {
        pageNum2020.getAndIncrement();
        log.info("pageNum2020={}", pageNum2020.get());
        List<Document2020Entity> list = document2020Mapper.findList(pageNum2020.get(), pageSize, criteria);
        log.info("size2020={}", list.size());
        for (Document2020Entity entity : list) {
            convert(entity);
        }
    }

    public void sync2022(Criteria criteria) {
        pageNum2022.getAndIncrement();
        log.info("pageNum2022={}", pageNum2022.get());
        List<Document2021_2022Entity> list = document2021_2021Mapper.findList(pageNum2022.get(), pageSize, criteria);
        log.info("size2022={}", list.size());
        for (Document2021_2022Entity entity : list) {
            convert(entity);
        }
    }

    private void convert(BaseEntity entity) {
        if (StringUtils.hasLength(entity.getCaseNo()) && StringUtils.hasLength(entity.getName()) && entity.getRefereeDate() != null) {
            DocumentEvasionEntity xsEntity = new DocumentEvasionEntity();
            BeanUtils.copyProperties(entity, xsEntity);
            DateTime dateTime = DateUtil.offsetHour(entity.getRefereeDate(), -8);
            String uid = entity.getName().trim() + entity.getCaseNo().trim() +dateTime.getTime();
            String md5 = MD5.create().digestHex(uid);
            xsEntity.setUid(md5);
            xsEntity.setJsonContent(entity.getJsonContent());
            try {
                documentEvasionMapper.insert(xsEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
