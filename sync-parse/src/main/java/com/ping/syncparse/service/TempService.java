package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.entity.BaseEntity;
import com.ping.syncparse.sync.c140.DocumentDqEntity;
import com.ping.syncparse.sync.c140.DocumentDqMapper;
import com.ping.syncparse.sync.c34.DocumentMsJtblEntity;
import com.ping.syncparse.sync.c34.DocumentMsMapper;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import com.ping.syncparse.sync.c8.Document8Entity;
import com.ping.syncparse.sync.c8.Document8Mapper;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.joining;

@Service
public class TempService {
    @Autowired
    private TempMapper tempMapper;
    @Autowired
    private DocumentMsMapper documentMapper;

    @Autowired
    private DocumentXsMapper documentXsMapper;

    @Autowired
    private Document8Mapper document8Mapper;

    @Autowired
    private DocumentDqMapper dqMapper;
    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();

    private List<Dict> caseTypes = new ArrayList<>();
    private Map<String, String> caseTypeMap = new HashMap<>();

    private int pageSize = 10000;
    private AtomicInteger pageNum = new AtomicInteger(135);


    {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:dict/*.txt");
            for (Resource resource : resources) {
                if ("docType.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    docTypes.addAll(JSON.parseArray(text, Dict.class));
                    for (Dict docType : docTypes) {
                        docTypeMap.put(docType.getCode().trim(), docType.getName());
                    }
                }

                if ("caseType.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    caseTypes.addAll(JSON.parseArray(text, Dict.class));
                    for (Dict caseType : caseTypes) {
                        caseTypeMap.put(caseType.getCode(), caseType.getName());
                    }
                }
            }
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public void convertMs() {
        List<TempVO> list = tempMapper.findList(1, 100, null);

        for (TempVO object : list) {
            DocumentMsJtblEntity entity = new DocumentMsJtblEntity();
            String id = object.getString("s5");
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
            object.remove("ayTree");
            object.remove("wsKey");
            object.remove("fyTree");
            object.remove("_id");
            object.remove("qwText");
            entity.setId(id);
            entity.setName(name);
            entity.setCaseNo(caseNo);
            entity.setCourtName(courtName);
            try {
                if (StringUtils.hasLength(refereeDate)) {
                    entity.setRefereeDate(DateUtil.offsetHour(DateUtil.parse(refereeDate, DateTimeFormatter.ISO_LOCAL_DATE), 8));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            entity.setCaseType(caseType);
            entity.setParty(partys);
            entity.setCause(causes);
            entity.setKeyword(keywords);
            if (caseTypeMap.containsKey(trialProceedings)) {
                entity.setTrialProceedings(caseTypeMap.get(trialProceedings));
            } else {
                entity.setTrialProceedings(trialProceedings);
            }

            entity.setDocType(docTypeMap.get(docType));
            entity.setJsonContent(object);
            entity.setHtmlContent(htmlContent);
            entity.setCreateTime(new Date());
            documentMapper.insert(entity);
        }
    }

    public void convertXs() {
        List<TempVO> list = tempMapper.findList(1, 100, null);

        for (TempVO object : list) {
            DocumentXsLhEntity entity = new DocumentXsLhEntity();
            String id = object.getString("s5");
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
            object.remove("ayTree");
            object.remove("wsKey");
            object.remove("fyTree");
            object.remove("_id");
            object.remove("qwText");
            entity.setId(id);
            entity.setName(name);
            entity.setCaseNo(caseNo);
            entity.setCourtName(courtName);
            try {
                if (StringUtils.hasLength(refereeDate)) {
                    entity.setRefereeDate(DateUtil.offsetHour(DateUtil.parse(refereeDate, DateTimeFormatter.ISO_LOCAL_DATE), 8));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            entity.setCaseType(caseType);
            entity.setParty(partys);
            entity.setCause(causes);
            entity.setKeyword(keywords);
            if (caseTypeMap.containsKey(trialProceedings)) {
                entity.setTrialProceedings(caseTypeMap.get(trialProceedings));
            } else {
                entity.setTrialProceedings(trialProceedings);
            }
            entity.setDocType(docTypeMap.get(docType));
            entity.setJsonContent(object);
            entity.setHtmlContent(htmlContent);
            entity.setCreateTime(new Date());
            documentXsMapper.insert(entity);
        }
    }

    public void convertXzxy() {
        List<TempVO> list = tempMapper.findList(1, 100, null);

        for (TempVO object : list) {
            Document8Entity entity = new Document8Entity();
            String id = object.getString("s5");
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
            object.remove("ayTree");
            object.remove("wsKey");
            object.remove("fyTree");
            object.remove("_id");
            object.remove("qwText");
            entity.setId(id);
            entity.setName(name);
            entity.setCaseNo(caseNo);
            entity.setCourtName(courtName);
            try {
                if (StringUtils.hasLength(refereeDate)) {
                    entity.setRefereeDate(DateUtil.offsetHour(DateUtil.parse(refereeDate, DateTimeFormatter.ISO_LOCAL_DATE), 8));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            String cause = null;
            if (causes != null) {
                cause = causes.stream().map(Object::toString).collect(joining(","));
                entity.setCause(cause);
            }
            String party = null;
            if (partys != null) {
                party = partys.stream().map(Object::toString).collect(joining(","));
                entity.setParty(party);
            }
            String keyword = null;
            if (keywords != null) {
                keyword = keywords.stream().map(Object::toString).collect(joining(","));
                entity.setKeyword(keyword);
            }
            entity.setCaseType(caseType);
            if (caseTypeMap.containsKey(trialProceedings)) {
                entity.setTrialProceedings(caseTypeMap.get(trialProceedings));
            } else {
                entity.setTrialProceedings(trialProceedings);
            }
            entity.setDocType(docTypeMap.get(docType));
            entity.setJsonContent(object);
            entity.setHtmlContent(htmlContent);
            entity.setCreateTime(new Date());
            document8Mapper.insert(entity);
        }
    }

    public void convertDq() {
        pageNum.getAndIncrement();
        System.out.println("pageNum=" + pageNum.get());
        List<TempVO> list = tempMapper.findList(pageNum.get(), pageSize, null);

        for (TempVO object : list) {
            DocumentDqEntity entity = new DocumentDqEntity();
            String id = object.getString("s5");
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
            object.remove("ayTree");
            object.remove("wsKey");
            object.remove("fyTree");
            object.remove("_id");
            object.remove("qwText");
            entity.setId(id);
            entity.setName(name);
            entity.setCaseNo(caseNo);
            entity.setCourtName(courtName);
            try {
                if (StringUtils.hasLength(refereeDate)) {
                    entity.setRefereeDate(DateUtil.offsetHour(DateUtil.parse(refereeDate, DateTimeFormatter.ISO_LOCAL_DATE), 8));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            entity.setCaseType(caseType);
            entity.setParty(partys);
            entity.setCause(causes);
            entity.setKeyword(keywords);
            if (caseTypeMap.containsKey(trialProceedings)) {
                entity.setTrialProceedings(caseTypeMap.get(trialProceedings));
            } else {
                entity.setTrialProceedings(trialProceedings);
            }
            entity.setDocType(docTypeMap.get(docType));
            entity.setJsonContent(object);
            entity.setHtmlContent(htmlContent);
            entity.setCreateTime(new Date());
            try {
                dqMapper.insert(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
