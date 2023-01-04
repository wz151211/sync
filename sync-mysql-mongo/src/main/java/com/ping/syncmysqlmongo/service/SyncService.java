package com.ping.syncmysqlmongo.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncmysqlmongo.common.Dict;
import com.ping.syncmysqlmongo.mongo.DocumentEntity;
import com.ping.syncmysqlmongo.mongo.MongoMapper;
import com.ping.syncmysqlmongo.mongo.entity.*;
import com.ping.syncmysqlmongo.mongo.mapper.*;
import com.ping.syncmysqlmongo.mysql.CpwsEntity;
import com.ping.syncmysqlmongo.mysql.CpwsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SyncService {

    @Autowired
    private MongoMapper mongoMapper;
    @Autowired
    private CpwsMapper cpwsMapper;

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

    @Value("${start}")
    private Integer start = 0;

    private Set<String> set = new HashSet<>();

    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();
    private AtomicInteger pageNum = new AtomicInteger(-1);

    {
        set.add("刑事");
        set.add("民事");
        set.add("行政");
        set.add("赔偿");
        set.add("执行");
        set.add("其他");
    }

    {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:doc/*.txt");
            for (Resource resource : resources) {
                if ("docType.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    docTypes.addAll(JSON.parseArray(text, Dict.class));
                    for (Dict docType : docTypes) {
                        docTypeMap.put(docType.getCode(), docType.getName());
                    }
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        if (pageNum.get() < 0) {
            pageNum.set(start);
        }
        if (pageNum.get() > start + 20) {
            pageNum.set(start);
        } else {
            pageNum.getAndIncrement();
        }
        log.info("pageNum={}", pageNum.get());
        List<CpwsEntity> entities = cpwsMapper.selectList(Wrappers.<CpwsEntity>lambdaQuery().ne(CpwsEntity::getFlag, 600).last("limit " + (pageNum.get() * 5000) + ", 5000"));
        if (entities == null || entities.size() == 0) {
            return;
        }

        entities.parallelStream().forEach(e -> {
            DocumentEntity entity = new DocumentEntity();
            entity.setId(e.getDocId());
            log.info("案件id={}", e.getId());
            JSONObject object = null;
            try {
                try {
                    object = JSON.parseObject(e.getCourtInfo());
                } catch (Exception ex) {
                    String str = e.getCourtInfo();
                    if (str.indexOf("#3!") > 0) {
                        str = str.replace("#3!", "\":\"");
                    } else if (str.indexOf(":#") > 0) {
                        str = str.replace(":#", ":\"");
                    } else if (str.indexOf("=!") > 0) {
                        str = str.replace("=!", ":\"");
                    } else if (str.indexOf(":!") > 0) {
                        str = str.replace(":!", ":\"");
                    }
                    try {
                        object = JSON.parseObject(str);
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        log.info("entity={}", entity);
                        cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 600).eq(CpwsEntity::getId, e.getId()));
                        throw new RuntimeException("解析JSON出错");
                    }

                }
                if (StringUtils.isNotEmpty(e.getCourtInfo())) {
                    if (e.getCourtInfo().contains("DocInfoVo")) {
                        String html = object.getJSONObject("DocInfoVo").getString("qwContent");
                        if (StringUtils.isNotEmpty(html)) {
                            Document parse = Jsoup.parse(html);
                            Elements divs = parse.getElementsByTag("div");
                            if (divs != null && divs.size() > 0) {
                                for (int i = 0; i < divs.size(); i++) {
                                    if (i >= 3) {
                                        continue;
                                    }
                                    Element element = divs.get(i);
                                    String text = element.ownText().trim();
                                    if (StringUtils.isNotEmpty(text)) {
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
                        entity.setCreateTime(new Date());
                        entity.setHtmlContent(html);
                    } else if (e.getCourtInfo().contains("Title")) {
                        String pubDate = object.getString("PubDate");
                        String title = object.getString("Title");
                        String html = object.getString("Html");
                        entity.setName(title);
                        try {
                            entity.setRefereeDate(DateUtil.parse(pubDate, DateTimeFormatter.ISO_LOCAL_DATE));
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        entity.setHtmlContent(html);
                        entity.setCreateTime(new Date());
                        if (StringUtils.isNotEmpty(html)) {
                            Document parse = Jsoup.parse(html);
                            Elements divs = parse.getElementsByTag("div");
                            if (divs != null && divs.size() > 0) {
                                for (int i = 0; i < divs.size(); i++) {
                                    if (i >= 3) {
                                        continue;
                                    }
                                    Element element = divs.get(i);
                                    String text = element.ownText().trim();
                                    if (StringUtils.isNotEmpty(text)) {
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
                    } else if (e.getCourtInfo().contains("qwContent")) {
                        String id = object.getString("s5");
                        if (StringUtils.isNotEmpty(id)) {
                            String name = object.getString("s1");
                            String caseNo = object.getString("s7");
                            String courtName = object.getString("s2");
                            String refereeDate = object.getString("s31");
                            String caseType = object.getString("s8");
                            String trialProceedings = object.getString("s9");
                            String docType = object.getString("s6");
                            JSONArray causes = object.getJSONArray("s11");
                            JSONArray partys = object.getJSONArray("s17");
                            JSONArray keywords = object.getJSONArray("s45");
                            String htmlContent = object.getString("qwContent");
                            object.remove("qwContent");
                            entity.setId(id);
                            entity.setName(name);
                            entity.setCaseNo(caseNo);
                            entity.setCourtName(courtName);
                            try {
                                entity.setRefereeDate(DateUtil.parse(refereeDate, DateTimeFormatter.ISO_LOCAL_DATE));
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                            entity.setCaseType(caseType);
                            entity.setParty(partys);
                            entity.setCause(causes);
                            entity.setKeyword(keywords);
                            entity.setTrialProceedings(trialProceedings);
                            entity.setDocType(docTypeMap.get(docType));
                            entity.setJsonContent(object);
                            entity.setHtmlContent(htmlContent);
                            entity.setCreateTime(new Date());
                        } else {
                            log.info("案件详情:{}", object);
                        }
                    }
                    log.info("id={},案件名称={}", entity.getId(), entity.getName());
                    mongoMapper.insert(entity);
                    build(entity);
                    cpwsMapper.deleteById(e.getId());
                } else {
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 600).eq(CpwsEntity::getId, e.getId()));
                }
            } catch (DuplicateKeyException ex) {
                log.info("1已存在id={}", entity.getId());
                build(entity);
                cpwsMapper.deleteById(e.getId());
            } catch (Exception ex) {
                if (ex.getMessage().contains("duplicate")) {
                    cpwsMapper.deleteById(e.getId());
                } else {
                    log.error("entity={}", entity);
                    log.error("!", ex);
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 600).eq(CpwsEntity::getId, e.getId()));
                }
                ex.printStackTrace();
            }

        });
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
        to.setCreateTime(new Date());
    }
}
