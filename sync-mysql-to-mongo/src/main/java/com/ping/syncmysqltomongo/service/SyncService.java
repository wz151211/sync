package com.ping.syncmysqltomongo.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncmysqltomongo.mongo.DocumentEntity;
import com.ping.syncmysqltomongo.mongo.MongoMapper;
import com.ping.syncmysqltomongo.mongo.entity.*;
import com.ping.syncmysqltomongo.mongo.mapper.*;
import com.ping.syncmysqltomongo.mysql.RemoteDocumentMapper;
import com.ping.syncmysqltomongo.mysql.RemotrDocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SyncService {
    @Autowired
    private MongoMapper mongoMapper;
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
    private DocumentOtherMapper documentOtherMapper;

    @Autowired
    private RemoteDocumentMapper remoteDocumentMapper;

    @Value("${start}")
    private Integer start = 0;

    private Set<String> set = new HashSet<>();
    private AtomicInteger pageNum = new AtomicInteger(-1);
    private int pageSize = 1000;
    private Date date = DateUtil.parse("2022-12-22", DateTimeFormatter.ISO_LOCAL_DATE).toJdkDate();

    {
        set.add("刑事");
        set.add("民事");
        set.add("行政");
        set.add("赔偿");
        set.add("执行");
        set.add("其他");
    }

    public void sync() {
        if (pageNum.get() < 0) {
            pageNum.set(start);
        }
        if (pageNum.get() > start + 20) {
            pageNum.set(start);
        } else {
            pageNum.getAndIncrement();
        }
        log.info("pageNum={}", pageNum.get());
        List<RemotrDocumentEntity> entities = remoteDocumentMapper.selectList(Wrappers.<RemotrDocumentEntity>lambdaQuery().last("limit " + (pageNum.get() * pageSize) + ", " + pageSize));
        entities.parallelStream().map(this::toEntity).forEach(c -> {
            log.info("id={},案件名称={}", c.getId(), c.getName());
            if (StringUtils.hasLength(c.getHtmlContent()) || (c.getJsonContent() != null && c.getJsonContent().size() > 0)) {
                try {
                    mongoMapper.insert(c);
                    build(c);
                    remoteDocumentMapper.update(null, Wrappers.<RemotrDocumentEntity>lambdaUpdate().set(RemotrDocumentEntity::getCreateTime, new Date()).eq(RemotrDocumentEntity::getId, c.getId()));
                } catch (DuplicateKeyException e) {
                    log.info("2已存在id={}", c.getId());
                    build(c);
                    remoteDocumentMapper.update(null, Wrappers.<RemotrDocumentEntity>lambdaUpdate().set(RemotrDocumentEntity::getCreateTime, new Date()).eq(RemotrDocumentEntity::getId, c.getId()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                log.info("删除案件信息为={}", c);
                remoteDocumentMapper.deleteById(c.getId());
            }
        });
    }

    private DocumentEntity toEntity(RemotrDocumentEntity documentEntity) {
        DocumentEntity entity = new DocumentEntity();
        entity.setId(documentEntity.getId());
        entity.setName(documentEntity.getName());
        entity.setCaseNo(documentEntity.getCaseNo());
        entity.setCourtName(documentEntity.getCourtName());
        try {
            if (StringUtils.hasText(documentEntity.getRefereeDate())) {
                entity.setRefereeDate(DateUtil.parse(documentEntity.getRefereeDate(), "yyy-MM-dd HH:mm:ss").setTimeZone(TimeZone.getDefault()).toJdkDate());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        entity.setCaseType(documentEntity.getCaseType());
        if (StringUtils.hasText(documentEntity.getCause())) {
            String[] split = documentEntity.getCause().split(",");
            entity.setCause(JSON.parseArray(JSON.toJSONString(split)));
        }
        entity.setDocType(documentEntity.getDocType());
        if ("刑 案件".equals(documentEntity.getCaseType())) {
            entity.setCaseType("刑事案件");
            entity.setDocType(entity.getDocType().replace(" ", "").substring(1));
        }
        if ("民 案件".equals(documentEntity.getCaseType())) {
            entity.setCaseType("民事案件");
            entity.setDocType(entity.getDocType().replace(" ", "").substring(1));

        }
        if ("行 案件".equals(documentEntity.getCaseType())) {
            entity.setCaseType("行政案件");
            entity.setDocType(entity.getDocType().replace(" ", "").substring(1));

        }
        if ("赔 案件".equals(documentEntity.getCaseType())) {
            entity.setCaseType("赔偿案件");
            entity.setDocType(entity.getDocType().replace(" ", "").substring(1));

        }
        if ("执 案件".equals(documentEntity.getCaseType())) {
            entity.setCaseType("执行案件");
            entity.setDocType(entity.getDocType().replace(" ", "").substring(1));

        }
        if ("其 案件".equals(documentEntity.getCaseType())) {
            entity.setCaseType("其他案件");
            entity.setDocType(entity.getDocType().replace(" ", "").substring(1));

        }
        if ("受 案件".equals(documentEntity.getCaseType())) {
            entity.setCaseType("执行案件");
            entity.setDocType(entity.getDocType().replace(" ", "").substring(1));

        }

        if (entity.getDocType() != null && entity.getDocType().length() >= 5) {
            if (StringUtils.hasText(documentEntity.getHtmlContent())) {
                Document parse = Jsoup.parse(documentEntity.getHtmlContent());
                Elements divs = parse.getElementsByTag("div");
                if (divs != null && divs.size() > 0) {
                    for (int i = 0; i < divs.size(); i++) {
                        if (i >= 4) {
                            continue;
                        }
                        Element element = divs.get(i);
                        String text = element.ownText().trim();
                        if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotEmpty(text)) {
                            if (text.contains("法院")) {
                                entity.setCourtName(text);
                            }
                            if (text.contains("书")) {
                                text = text.replace(" ", "");
                                String s = text.substring(0, 2);
                                if (set.contains(s)) {
                                    entity.setCaseType(s + "案件");
                                    entity.setDocType(text.substring(2));
                                } else {
                                    entity.setDocType(text);
                                }
                            }
                            if (text.contains("号")) {
                                entity.setCaseNo(text);
                            }
                        }
                    }
                }
            }
        }

        entity.setTrialProceedings(documentEntity.getTrialProceedings());
        entity.setHtmlContent(documentEntity.getHtmlContent());
        entity.setJsonContent(JSON.parseObject(documentEntity.getJsonContent()));
        if (StringUtils.hasText(documentEntity.getParty())) {
            String party = documentEntity.getParty();
            party = party.replace("；", ",");
            party = party.replace(";", ",");
            entity.setParty(JSON.parseArray(JSON.toJSONString(party.split(","))));
        }
        if (StringUtils.hasText(documentEntity.getKeyword())) {
            String[] split = documentEntity.getKeyword().split(",");
            entity.setKeyword(JSON.parseArray(JSON.toJSONString(split)));
        }
        entity.setCreateTime(new Date());
        return entity;
    }

    public void build(DocumentEntity c) {
        if (c.getRefereeDate() != null) {
            if (DateUtil.parse("2014-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                Document1Entity entity = new Document1Entity();
                toEntity(c, entity);
                try {
                    document1Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (DateUtil.parse("2015-01-01 00:00:00").isBefore(c.getRefereeDate())
                    && DateUtil.parse("2016-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                Document2Entity entity = new Document2Entity();
                toEntity(c, entity);
                try {
                    document2Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (DateUtil.parse("2017-01-01 00:00:00").isBefore(c.getRefereeDate())
                    && DateUtil.parse("2017-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                Document3Entity entity = new Document3Entity();
                toEntity(c, entity);
                try {
                    document3Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (DateUtil.parse("2018-01-01 00:00:00").isBefore(c.getRefereeDate())
                    && DateUtil.parse("2018-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                Document4Entity entity = new Document4Entity();
                toEntity(c, entity);
                try {
                    document4Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (DateUtil.parse("2019-01-01 00:00:00").isBefore(c.getRefereeDate())
                    && DateUtil.parse("2019-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                Document5Entity entity = new Document5Entity();
                toEntity(c, entity);
                try {
                    document5Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (DateUtil.parse("2020-01-01 00:00:00").isBefore(c.getRefereeDate())
                    && DateUtil.parse("2020-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                Document6Entity entity = new Document6Entity();
                toEntity(c, entity);
                try {
                    document6Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (DateUtil.parse("2021-01-01 00:00:00").isBefore(c.getRefereeDate())
                    && DateUtil.parse("2022-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                Document7Entity entity = new Document7Entity();
                toEntity(c, entity);
                try {
                    document7Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else if (org.springframework.util.StringUtils.hasText(c.getCaseNo())) {
            if (c.getCaseNo().contains("2015") || c.getCaseNo().contains("2016")) {
                Document2Entity entity = new Document2Entity();
                toEntity(c, entity);
                try {
                    document2Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (c.getCaseNo().contains("2017")) {
                Document3Entity entity = new Document3Entity();
                toEntity(c, entity);
                try {
                    document3Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (c.getCaseNo().contains("2018")) {
                Document4Entity entity = new Document4Entity();
                toEntity(c, entity);
                try {
                    document4Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (c.getCaseNo().contains("2019")) {
                Document5Entity entity = new Document5Entity();
                toEntity(c, entity);
                try {
                    document5Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (c.getCaseNo().contains("2020")) {
                Document6Entity entity = new Document6Entity();
                toEntity(c, entity);
                try {
                    document6Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else if (c.getCaseNo().contains("2021") || c.getCaseNo().contains("2022")) {
                Document7Entity entity = new Document7Entity();
                toEntity(c, entity);
                try {
                    document7Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Document1Entity entity = new Document1Entity();
                toEntity(c, entity);
                try {
                    document1Mapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        } else {
            DocumentOtherEntity entity = new DocumentOtherEntity();
            toEntity(c, entity);
            try {
                documentOtherMapper.insert(entity);
            } catch (DuplicateKeyException ex) {
                log.info("已存在id={}", entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    private void toEntity(DocumentEntity from, BaseEntity to) {
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
       // to.setCreateTime(new Date());
    }
}
