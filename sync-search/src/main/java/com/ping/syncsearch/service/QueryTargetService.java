package com.ping.syncsearch.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncsearch.entity.*;
import com.ping.syncsearch.mapper.*;
import com.ping.syncsearch.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class QueryTargetService {
    @Autowired
    private Document2014Mapper document2014Mapper;
    @Autowired
    private Document2015Mapper document2015Mapper;
    @Autowired
    private Document2016Mapper document2016Mapper;
    @Autowired
    private Document2017Mapper document2017Mapper;
    @Autowired
    private Document2018Mapper document2018Mapper;
    @Autowired
    private Document2019Mapper document2019Mapper;
    @Autowired
    private Document2020Mapper document2020Mapper;
    @Autowired
    private Document2021Mapper document2021Mapper;
    @Autowired
    private Document2022Mapper document2022Mapper;
    @Autowired
    private Document2023Mapper document2023Mapper;
    @Autowired
    private DocumentTargetMapper targetMapper;

    private AtomicInteger pageNum2014 = new AtomicInteger(-1);
    private AtomicInteger pageNum2015 = new AtomicInteger(-1);
    private AtomicInteger pageNum2016 = new AtomicInteger(-1);
    private AtomicInteger pageNum2017 = new AtomicInteger(-1);
    private AtomicInteger pageNum2018 = new AtomicInteger(-1);
    private AtomicInteger pageNum2019 = new AtomicInteger(-1);
    private AtomicInteger pageNum2020 = new AtomicInteger(-1);
    private AtomicInteger pageNum2021 = new AtomicInteger(-1);
    private AtomicInteger pageNum2022 = new AtomicInteger(-1);
    private AtomicInteger pageNum2023 = new AtomicInteger(-1);
    private int pageSize = 10000;


    public void sync2014(Criteria criteria) {
        pageNum2014.getAndIncrement();
        log.info("pageNum2014={}", pageNum2014.get());
        List<Document2014Entity> list = document2014Mapper.findList(pageNum2014.get(), pageSize, criteria);
        log.info("size2014={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2015(Criteria criteria) {
        pageNum2015.getAndIncrement();
        log.info("pageNum2015={}", pageNum2015.get());
        List<Document2015Entity> list = document2015Mapper.findList(pageNum2015.get(), pageSize, criteria);
        log.info("size2015={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2016(Criteria criteria) {
        pageNum2016.getAndIncrement();
        log.info("pageNum2016={}", pageNum2016.get());
        List<Document2016Entity> list = document2016Mapper.findList(pageNum2016.get(), pageSize, criteria);
        log.info("size2016={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2017(Criteria criteria) {
        pageNum2017.getAndIncrement();
        log.info("pageNum2017={}", pageNum2017.get());
        List<Document2017Entity> list = document2017Mapper.findList(pageNum2017.get(), pageSize, criteria);
        log.info("size2017={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2018(Criteria criteria) {
        pageNum2018.getAndIncrement();
        log.info("pageNum2018={}", pageNum2018.get());
        List<Document2018Entity> list = document2018Mapper.findList(pageNum2018.get(), pageSize, criteria);
        log.info("size2018={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2019(Criteria criteria) {
        pageNum2019.getAndIncrement();
        log.info("pageNum2019={}", pageNum2019.get());
        List<Document2019Entity> list = document2019Mapper.findList(pageNum2019.get(), pageSize, criteria);
        log.info("size2019={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2020(Criteria criteria) {
        pageNum2020.getAndIncrement();
        log.info("pageNum2020={}", pageNum2020.get());
        List<Document2020Entity> list = document2020Mapper.findList(pageNum2020.get(), pageSize, criteria);
        log.info("size2020={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2021(Criteria criteria) {
        pageNum2021.getAndIncrement();
        log.info("pageNum2021={}", pageNum2021.get());
        List<Document2021Entity> list = document2021Mapper.findList(pageNum2021.get(), pageSize, criteria);
        log.info("size2021={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2022(Criteria criteria) {
        pageNum2022.getAndIncrement();
        log.info("pageNum2022={}", pageNum2022.get());
        List<Document2022Entity> list = document2022Mapper.findList(pageNum2022.get(), pageSize, criteria);
        log.info("size2022={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void sync2023(Criteria criteria) {
        pageNum2023.getAndIncrement();
        log.info("pageNum2023={}", pageNum2023.get());
        List<Document2023Entity> list = document2023Mapper.findList(pageNum2023.get(), pageSize, criteria);
        log.info("size2023={}", list.size());
        list.parallelStream().map(this::convert).forEach(c -> {
            try {
                targetMapper.insert(c);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private DocumentTargetEntity convert(JSONObject from) {
        DocumentTargetEntity entity = new DocumentTargetEntity();
        entity.fluentPutAll(from);
        return entity;
    }
}
