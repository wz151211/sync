package com.ping.syncmysql.task.local;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncmysql.task.Dict;
import com.ping.syncmysql.task.remote.RemoteDocumentService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
@DS("dm1")
public class UpdateDocumentService {

    @Autowired
    private RemoteDocumentService remoteDocumentService;
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private CpwsMapper cpwsMapper;

    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();
    private DateTime dateTime = null;
    private AtomicInteger days = new AtomicInteger(0);

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

/*        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
        Date start = Date.from(localDate.minusDays(days.get() + intervalDays).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(localDate.minusDays(days.get()).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        log.info("查询时间为start={},end={}", DateUtil.format(start, "yyyy-MM-dd"), DateUtil.format(end, "yyyy-MM-dd"));*/
        List<CpwsEntity> entities = cpwsMapper.selectList(Wrappers.<CpwsEntity>lambdaQuery().eq(CpwsEntity::getFlag, 0).last("limit " + (days.get() * 5000) + ", 5000"));
        days.getAndIncrement();
        if (days.get() > 20) {
            days.set(0);
        }
        log.info("days={}", days.get());
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
                    log.info("entity={}", entity);
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 4).eq(CpwsEntity::getId, e.getId()));
                    throw new RuntimeException("解析JSON出错");
                }
                if (StringUtils.isNotEmpty(e.getCourtInfo())) {
                    if (e.getCourtInfo().contains("DocInfoVo")) {
                        String html = object.getJSONObject("DocInfoVo").getString("qwContent");
                        if (StringUtils.isNotEmpty(html)) {
                            Document parse = Jsoup.parse(html);
                            Elements divs = parse.getElementsByTag("div");
                            if (divs != null && divs.size() > 0) {
                                for (int i = 0; i < divs.size(); i++) {
                                    if (i >= 4) {
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
                                            entity.setCaseType(text.substring(0, 2) + "案件");
                                            entity.setDocType(text.substring(2));
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
                        entity.setRefereeDate(pubDate);
                        entity.setHtmlContent(html);
                        entity.setCreateTime(new Date());
                        if (StringUtils.isNotEmpty(html)) {
                            Document parse = Jsoup.parse(html);
                            Elements divs = parse.getElementsByTag("div");
                            if (divs != null && divs.size() > 0) {
                                for (int i = 0; i < divs.size(); i++) {
                                    if (i >= 4) {
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
                                            entity.setCaseType(text.substring(0, 2) + "案件");
                                            entity.setDocType(text.substring(2));
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
                            String cause = null;
                            if (causes != null) {
                                cause = causes.stream().map(Object::toString).collect(joining(","));
                            }
                            JSONArray partys = object.getJSONArray("s17");
                            String party = null;
                            if (partys != null) {
                                party = partys.stream().map(Object::toString).collect(joining(","));
                            }
                            JSONArray keywords = object.getJSONArray("s45");
                            String keyword = null;
                            if (keywords != null) {
                                keyword = keywords.stream().map(Object::toString).collect(joining(","));
                            }
                            String courtConsidered = object.getString("s26");
                            String judgmentResult = object.getString("s27");
                            String htmlContent = object.getString("qwContent");
                            object.remove("qwContent");
                            String jsonContent = object.toJSONString();
                            // log.info("案件名称={}", name);
                            entity.setId(id);
                            entity.setName(name);
                            entity.setCaseNo(caseNo);
                            entity.setCourtName(courtName);
                            entity.setRefereeDate(refereeDate);
                            entity.setCaseType(caseType);
                            entity.setParty(party);
                            entity.setCause(cause);
                            entity.setJudgmentResult(judgmentResult);
                            entity.setKeyword(keyword);
                            entity.setCourtConsidered(courtConsidered);
                            entity.setTrialProceedings(trialProceedings);
                            entity.setDocType(docTypeMap.get(docType));
                            entity.setJsonContent(jsonContent);
                            entity.setHtmlContent(htmlContent);
                            entity.setCreateTime(new Date());
                        } else {
                            log.info("案件详情:{}", object);
                        }
                    }
                    log.info("案件名称={}", entity.getName());
                    // documentMapper.insert(entity);
                    remoteDocumentService.save(entity);
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 1).eq(CpwsEntity::getId, e.getId()));
                } else {
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 4).eq(CpwsEntity::getId, e.getId()));
                }
            } catch (Exception ex) {

                if (ex.getMessage().contains("Duplicate")) {
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 1).eq(CpwsEntity::getId, e.getId()));
                } else {
                    log.error("entity={}", entity);
                    log.error("错误信息", ex);
                    cpwsMapper.update(null, Wrappers.<CpwsEntity>lambdaUpdate().set(CpwsEntity::getFlag, 4).eq(CpwsEntity::getId, e.getId()));

                }
                ex.printStackTrace();
            }

        });
    }
}
