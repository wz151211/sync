package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ping.syncparse.common.Dict;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class TempService {
    @Autowired
    private TempMapper tempMapper;

    @Autowired
    private InternetFraudMapper mapper;

    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();

    private List<Dict> caseTypes = new ArrayList<>();
    private Map<String, String> caseTypeMap = new HashMap<>();

    private int pageSize = 10000;
    private AtomicInteger pageNum = new AtomicInteger(294);


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

    public void convert() {
        pageNum.getAndIncrement();
        log.info("pageNum={}", pageNum.get());
        List<TempVO> list = tempMapper.findList(pageNum.get(), pageSize, null);
        for (TempVO object : list) {
            InternetFraudEntity entity = new InternetFraudEntity();
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
                mapper.insert(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void count() {
        Criteria criteria = Criteria.where("caseType").is("民事案件").and("docType").is("判决书").and("trialProceedings").is("民事一审").and("cause").is("离婚纠纷");
        Long count = tempMapper.getCount(criteria);
        System.out.println("数量=" + count);
    }
}
