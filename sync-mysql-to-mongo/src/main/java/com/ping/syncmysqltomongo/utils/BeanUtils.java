package com.ping.syncmysqltomongo.utils;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncmysqltomongo.mongo.entity.BaseEntity;
import com.ping.syncmysqltomongo.vo.Dict;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BeanUtils {
    private static List<Dict> docTypes = new ArrayList<>();
    private static Map<String, String> docTypeMap = new HashMap<>();

    private static List<Dict> caseTypes = new ArrayList<>();
    private static Map<String, String> caseTypeMap = new HashMap<>();

    static {
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

    public static <T, E> E convert(T from, Supplier<E> supplier) {
        Assert.notNull(from, "源对象不能为空");
        E target = supplier.get();
        org.springframework.beans.BeanUtils.copyProperties(from, target);
        return target;
    }

    public static BaseEntity toEntity(JSONObject from) {
        BaseEntity entity = new BaseEntity();
        String id = from.getString("_id");
        String name = from.getString("s1");
        String caseNo = from.getString("s7");
        String courtName = from.getString("s2");
        String refereeDate = from.getString("s31");
        String caseType = from.getString("s8");
        String trialProceedings = from.getString("s9");
        String docType = from.getString("s6");
        JSONArray causes = from.getJSONArray("s11");
        JSONArray partys = from.getJSONArray("s17");
        JSONArray keywords = from.getJSONArray("s45");
        JSONArray legalBasis = from.getJSONArray("s47");
        String judgmentResult = from.getString("s27");
        String courtConsidered = from.getString("s26");
        String litigationRecords = from.getString("s23");
        String fact = from.getString("s25");
        JSONArray areas = from.getJSONArray("fyTree");
        String htmlContent = from.getString("qwContent");
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
        entity.setJsonContent(from);
        entity.setHtmlContent(htmlContent);
        entity.setJudgmentResult(judgmentResult);
        entity.setCourtConsidered(courtConsidered);
        entity.setLitigationRecords(litigationRecords);
        entity.setLegalBasis(legalBasis);
        entity.setFact(fact);
        try {
            entity.setProvince(areas.getString(0));
            entity.setCity(areas.getString(1));
            entity.setCounty(areas.getString(2));
        } catch (Exception e) {

        }
        return entity;
    }
}
