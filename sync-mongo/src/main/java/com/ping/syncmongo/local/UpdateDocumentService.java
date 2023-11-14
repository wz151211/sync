package com.ping.syncmongo.local;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncmongo.common.model.Dict;
import com.ping.syncmongo.local.entity.CpwsEntity;
import com.ping.syncmongo.local.mapper.CpwsMapper;
import com.ping.syncmongo.remote.DocumentEntity;
import com.ping.syncmongo.remote.RemoteDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
public class UpdateDocumentService {
    @Autowired
    private RemoteDocumentMapper documentMapper;
    @Autowired
    private CpwsMapper cpwsMapper;

    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();
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
        List<CpwsEntity> entities = cpwsMapper.selectList(2000, (days.get() * 2000));
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
            JSONObject object = null;
            try {
                try {
                    object = JSON.parseObject(e.getCourtInfo());
                } catch (Exception ex) {
                    log.info("entity={}", entity);
                    cpwsMapper.update(e.getOId(), "400");
                    throw new RuntimeException("解析JSON出错");
                }
                if (StringUtils.isNotEmpty(e.getCourtInfo())) {
                    if (e.getCourtInfo().contains("DocInfoVo")) {
                        String html = object.getJSONObject("DocInfoVo").getString("qwContent");
                        if (StringUtils.isNotEmpty(html)) {
                            Document parse = Jsoup.parse(html);
                            org.jsoup.select.Elements divs = parse.getElementsByTag("div");
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
                                            break;
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
                                            entity.setCaseType(text.substring(0, 3) + "案件");
                                            entity.setDocType(text.substring(3));
                                        }
                                        if (text.contains("号")) {
                                            entity.setCaseNo(text);
                                            break;
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
                            // log.info("案件名称={}", name);
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
                    log.info("案件id={},案件名称={}", entity.getId(), entity.getName());
                    documentMapper.save(entity);
                    cpwsMapper.remove(e.getOId());
                } else {
                    cpwsMapper.update(e.getOId(), "400");
                }
            } catch (Exception ex) {

                if (ex.getMessage().contains("duplicate")) {
                    cpwsMapper.remove(e.getOId());
                } else {
                    log.error("entity={}", entity);
                    log.error("错误信息", ex);
                    cpwsMapper.update(e.getOId(), "400");

                }
                ex.printStackTrace();
            }

        });
    }
}
