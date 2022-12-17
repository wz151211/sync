package com.ping.syncmysqltomongo.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncmysqltomongo.mongo.DocumentEntity;
import com.ping.syncmysqltomongo.mongo.MongoMapper;
import com.ping.syncmysqltomongo.mysql.RemoteDocumentMapper;
import com.ping.syncmysqltomongo.mysql.RemotrDocumentEntity;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
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
    private RemoteDocumentMapper remoteDocumentMapper;


    private Set<String> set = new HashSet<>();
    private AtomicInteger pageNum = new AtomicInteger(0);
    private int pageSize = 1000;
    private Date date = DateUtil.parse("2022-12-14", DateTimeFormatter.ISO_LOCAL_DATE).toJdkDate();

    {
        set.add("刑事");
        set.add("民事");
        set.add("行政");
        set.add("赔偿");
        set.add("执行");
        set.add("其他");
    }

    public void sync() {
        log.info("pageNum={}", pageNum.get());
        if (pageNum.get() > 20) {
            pageNum.set(0);
        } else {
            pageNum.getAndIncrement();
        }
        List<RemotrDocumentEntity> entities = remoteDocumentMapper.selectList(Wrappers.<RemotrDocumentEntity>lambdaQuery().lt(RemotrDocumentEntity::getCreateTime, date).last("limit " + (pageNum.get() * pageSize) + ", " + pageSize));
        entities.parallelStream().map(this::toEntity).forEach(c -> {
            log.info("id={},案件名称={}", c.getId(), c.getName());
            mongoMapper.insert(c);
            remoteDocumentMapper.update(null, Wrappers.<RemotrDocumentEntity>lambdaUpdate().set(RemotrDocumentEntity::getCreateTime, new Date()).eq(RemotrDocumentEntity::getId, c.getId()));
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
            entity.setCause(documentEntity.getCause().split(","));
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

        if (entity.getDocType().length() >= 5) {
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
        entity.setJsonContent(JSONUtil.parseObj(documentEntity.getJsonContent()));
        if (StringUtils.hasText(documentEntity.getParty())) {
            String party = documentEntity.getParty();
            party = party.replace("；", ",");
            party = party.replace(";", ",");
            entity.setParty(party.split(","));
        }
        if (StringUtils.hasText(documentEntity.getKeyword())) {
            entity.setKeyword(documentEntity.getKeyword().split(","));
        }
        entity.setCreateTime(new Date());
        return entity;
    }
}
