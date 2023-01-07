package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.sync.c34.DocumentMsJtblEntity;
import com.ping.syncparse.sync.c34.DocumentMsMapper;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
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

import static java.util.stream.Collectors.*;

/**
 * @Author: W.Z
 * @Date: 2022/12/15 22:09
 */
@Service
@Slf4j
public class ParsePartyService {
    @Autowired
    private DocumentMsMapper documentMapper;

    @Autowired
    private DocumentXsMapper documentXsMapper;
    @Autowired
    private CaseMapper caseMapper;

    private AtomicInteger pageNum = new AtomicInteger(0);

    private List<Dict> causeList = new ArrayList<>();
    private List<Dict> areaList = new ArrayList<>();
    Map<String, Dict> areaMap = new HashMap<>();
    Map<String, Dict> areaCodeMap = new HashMap<>();

    private String[] temp = {"汉族", "满族", "回族", "藏族", "苗族", "彝族", "壮族", "侗族", "瑶族", "白族", "傣族", "黎族", "佤族", "畲族", "水族", "土族", "蒙古族", "布依族", "土家族", "哈尼族", "傈僳族", "高山族", "拉祜族", "东乡族", "纳西族", "景颇族", "哈萨克族", "维吾尔族", "达斡尔族", "柯尔克孜族", "羌族", "怒族", "京族", "德昂族", "保安族", "裕固族", "仫佬族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "朝鲜族", "赫哲族", "门巴族", "珞巴族", "独龙族", "基诺族", "塔吉克族", "俄罗斯族", "鄂温克族", "塔塔尔族", "鄂伦春族", "乌孜别克族"};

    private int pageSize = 1000;
    private Set<String> nations = new HashSet<>();
    private Set<String> causeSet = new HashSet<>();

    {
        nations.addAll(Arrays.asList(temp));
    }

    {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:dict/*.txt");
            for (Resource resource : resources) {
                if ("ay.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    causeList.addAll(JSON.parseArray(text, Dict.class));
                }

                if ("area.txt".contains(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    areaList.addAll(JSON.parseArray(text, Dict.class));
                }
            }
            causeSet = causeList.stream().map(Dict::getName).collect(toSet());
            areaMap = areaList.parallelStream().peek(c -> {
                String name = c.getName();
                int start = name.indexOf("(");
                c.setName(name.substring(0, start));
            }).collect(toMap(Dict::getName, c -> c, (k1, k2) -> k2));
            areaCodeMap = areaList.parallelStream().collect(toMap(Dict::getCode, c -> c, (k1, k2) -> k2));


        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public void parse() {
        Criteria criteria = Criteria.where("name").regex("判决书");
        //  List<DocumentMsJtblEntity> entities = documentMapper.findList(1, pageSize, null);
        List<DocumentXsLhEntity> entities = documentXsMapper.findList(pageNum.get(), pageSize, criteria);
        pageNum.getAndIncrement();
        for (DocumentXsLhEntity entity : entities) {
            CaseVo vo = new CaseVo();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            vo.setCaseNo(entity.getCaseNo());
            vo.setCourtName(entity.getCourtName());
            vo.setHtmlContent(entity.getHtmlContent());
            vo.setJsonContent(entity.getJsonContent());
            if (StringUtils.hasLength(entity.getCourtName())) {
                if (entity.getCourtName().contains("省")) {

                    vo.setProvince(entity.getCourtName().substring(0, entity.getCourtName().indexOf("省") + 1));

                } else if (entity.getCourtName().contains("市")) {
                    String s = entity.getCourtName().substring(0, entity.getCourtName().indexOf("市") + 1);
                    Dict dict = areaMap.get(s);
                    if (dict != null) {
                        if (dict.getPId().equals("-1")) {
                            vo.setProvince(dict.getName());
                        } else {
                            Dict dict1 = areaCodeMap.get(dict.getCode());
                            if (dict1.getPId().equals("-1")) {
                                vo.setProvince(dict1.getName());
                            }
                        }
                    } else {
                        vo.setProvince(s);
                    }


                } else if (entity.getCourtName().contains("区")) {
                    String s = entity.getCourtName().substring(0, entity.getCourtName().indexOf("区") + 1);
                    Dict dict = areaMap.get(s);
                    if (dict != null) {
                        if (dict.getPId().equals("-1")) {
                            vo.setProvince(dict.getName());
                        } else {
                            Dict dict1 = areaCodeMap.get(dict.getCode());
                            if (dict1.getPId().equals("-1")) {
                                vo.setProvince(dict1.getName());
                            } else {
                                Dict dict2 = areaCodeMap.get(dict1.getCode());
                                if (dict2.getPId().equals("-1")) {
                                    vo.setProvince(dict2.getName());
                                }
                            }
                        }
                    } else {
                        vo.setProvince(s);
                    }

                } else if (entity.getCourtName().contains("县")) {

                    String s = entity.getCourtName().substring(0, entity.getCourtName().indexOf("县") + 1);

                    Dict dict = areaMap.get(s);
                    if (dict != null) {
                        if (dict.getPId().equals("-1")) {
                            vo.setProvince(dict.getName());
                        } else {
                            Dict dict1 = areaCodeMap.get(dict.getCode());
                            if (dict1.getPId().equals("-1")) {
                                vo.setProvince(dict1.getName());
                            } else {
                                Dict dict2 = areaCodeMap.get(dict1.getCode());
                                if (dict2.getPId().equals("-1")) {
                                    vo.setProvince(dict2.getName());
                                }
                            }
                        }
                    } else {
                        vo.setProvince(s);
                    }

                }
            }

            if (entity.getJsonContent() != null && entity.getJsonContent().size() > 0) {
                PartyEntity party = null;
                JSONObject object = entity.getJsonContent();
                String trialProceedings = object.getString("s9");
                JSONArray causes = object.getJSONArray("s11");
                String refereeDate = object.getString("s31");
                try {
                    vo.setRefereeDate(DateUtil.parse(refereeDate, DateTimeFormatter.ISO_LOCAL_DATE));
                    vo.setRefereeDate(DateUtil.offsetHour(vo.getRefereeDate(), 8));

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                vo.setTrialProceedings(trialProceedings);
                if (causes != null && causes.size() > 0) {
                    vo.setCause(causes.stream().map(Object::toString).collect(joining(",")));

                }
                JSONArray array = object.getJSONArray("s17");
                if (array != null && array.size() > 0) {
                    if (StringUtils.hasLength(entity.getHtmlContent())) {
                        Document parse = Jsoup.parse(entity.getHtmlContent());
                        Elements elements = parse.select(".PDF_pox");
                        if (elements == null || elements.size() == 0) {
                            elements = parse.select("div");
                        }

                        for (Object o : array) {
                            for (int i = 0; i < elements.size(); i++) {
                                Element element = elements.get(i);
                                String text = element.ownText();
                                if (text.contains(o.toString())) {
                                    party = parseText(text, o.toString());
                                    vo.getParty().add(party);
                                    break;
                                }
                            }
                        }
                    }
                    if ("民事案件".equals(entity.getCaseType())) {
                        String s25 = object.getString("s25");
                        String s26 = object.getString("s26");
                        if (StringUtils.hasLength(s25)) {
                            s25 = s25.replace("，", ",");
                            s25 = s25.replace("；", ",");
                            s25 = s25.replace("。", ",");
                            String[] split = s25.split(",");

                            for (String s : split) {
                                if (s.contains("登记结婚") && s.contains("年") && s.contains("月") && s.contains("日")) {
                                    try {
                                        vo.setMarriagContent(s);
                                        String date = s.substring(s.indexOf("年") - 4, s.indexOf("日") + 1);
                                        vo.setMarriagDate(date);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                if ((!s.contains("未生育") && !s.contains("不能生育") && !s.contains("没有生育")) && (s.contains("生育") || s.contains("生下") || s.contains("育有"))) {
                                    vo.setHaveChildren("是");
                                    vo.setChildrenContent(s);
                                }
                                if ((!s.contains("未再婚") && !s.contains("没有再婚")) && s.contains("再婚")) {
                                    vo.setRemarry("是");
                                    vo.setRemarryContent(s);
                                }
                            }
                        }

                        if (StringUtils.hasLength(s26)) {
                            s26 = s26.replace("，", ",");
                            s26 = s26.replace("；", ",");
                            s26 = s26.replace("。", ",");
                            String[] split = s26.split(",");

                            for (String s : split) {
                                if (!StringUtils.hasLength(vo.getMarriagDate())) {
                                    if (s.contains("登记结婚") && s.contains("年") && s.contains("月") && s.contains("日")) {
                                        try {
                                            vo.setMarriagContent(s);
                                            String date = s.substring(s.indexOf("年") - 4, s.indexOf("日") + 1);
                                            vo.setMarriagDate(date);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }

                                if (!StringUtils.hasLength(vo.getHaveChildren())) {
                                    if ((!s.contains("未生育") && !s.contains("不能生育") && !s.contains("没有生育")) &&
                                            (s.contains("生育") || s.contains("生下") || s.contains("育有"))) {
                                        vo.setHaveChildren("是");
                                        vo.setChildrenContent(s);
                                    } else {
                                        vo.setHaveChildren("否");

                                    }
                                }

                                if (!StringUtils.hasLength(vo.getRemarry())) {
                                    if ((!s.contains("未再婚") && !s.contains("没有再婚")) && s.contains("再婚")) {
                                        vo.setRemarry("是");
                                        vo.setRemarryContent(s);
                                    } else {
                                        vo.setRemarry("否");
                                    }
                                }
                            }
                        }

                        String s27 = object.getString("s27");
                        // String replace = s27.replace("；", "。");
                        if (StringUtils.hasLength(s27)) {
                            String[] split = s27.split("。");
                            for (String s : split) {
                                if (s.contains("不准予") || s.contains("驳回") || s.contains("不准") || s.contains("不予准许") || s.contains("不予支持") || s.contains("婚姻无效")) {
                                    vo.setJudgmenResultContent(s);
                                    vo.setJudgmenResult("否");
                                    break;
                                } else if (s.contains("准予") || s.contains("同意") || s.contains("准许") || (s.contains("准") && s.contains("离婚"))) {
                                    vo.setJudgmenResultContent(s);
                                    vo.setJudgmenResult("是");
                                    break;
                                }
                            }


                        } else {
                            vo.setJudgmenResult("否");

                        }

                    }

                    if ("刑事案件".equals(entity.getCaseType())) {
                        String s27 = object.getString("s27");
                        vo.setJudgmenResultContent(s27);
                        StringBuilder result = new StringBuilder();
                        if (StringUtils.hasLength(s27)) {
                            String replace = s27.replace("；", "。");
                            replace = replace.replace("\n", "。");
                            String[] split = replace.split("。");
                            if (array != null && array.size() > 0) {
                                for (Object o : array) {
                                    for (String s : split) {
                                        if (s.contains(o.toString()) && s.contains("犯") && s.contains("罪")) {
                                            CrimeVO crimeVO = new CrimeVO();
                                            crimeVO.setName(o.toString());
                                            for (String cause : causeSet) {
                                                if (s.contains(cause)) {
                                                    if (StringUtils.hasLength(crimeVO.getCrime())) {
                                                        crimeVO.setCrime(crimeVO.getCrime() + "、" + cause);
                                                    } else {
                                                        crimeVO.setCrime(cause);
                                                    }
                                                }
                                            }
                                            try {
                                                if (s.contains("有期徒刑") && s.contains("月")) {
                                                    int start = s.indexOf("有期徒刑");
                                                    int end = s.indexOf("月") + 1;
                                                    String s1 = s.substring(start, end);
                                                    crimeVO.setImprisonmentTerm(s1);
                                                } else if (s.contains("有期徒刑") && s.contains("年")) {
                                                    String s1 = s.substring(s.indexOf("有期徒刑"), s.lastIndexOf("年") + 1);
                                                    crimeVO.setImprisonmentTerm(s1);
                                                } else if (s.contains("死刑")) {
                                                    crimeVO.setImprisonmentTerm("死刑");
                                                } else if (s.contains("无期徒刑")) {
                                                    crimeVO.setImprisonmentTerm("无期徒刑");
                                                } else if (s.contains("拘役") && s.contains("月")) {
                                                    String s1 = s.substring(s.indexOf("拘役"), s.lastIndexOf("月") + 1);
                                                    crimeVO.setImprisonmentTerm(s1);
                                                } else if (s.contains("免予刑事处罚")) {
                                                    crimeVO.setImprisonmentTerm("免予刑事处罚");
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                            vo.getCrimes().add(crimeVO);
                                            break;
                                        }
                                    }

                                }
                                if (StringUtils.hasLength(result.toString())) {
                                    vo.setJudgmenResult(result.toString().substring(0, result.length() - 1));

                                }
                            }
                        }
                    }

                }

            } else {

         /*       Document parse = Jsoup.parse(entity.getHtmlContent());
                Elements elements = parse.select(".PDF_pox");
                if (elements == null || elements.size() == 0) {
                    elements = parse.select("div");
                }
                PartyEntity party = null;
                for (int i = 3; i < elements.size(); i++) {
                    Element element = elements.get(i);
                    String text = element.ownText();
                    if (text.contains("被告")) {
                        party = parseText(text, "");
                        vo.getParty().add(party);
                        break;
                    }

                }*/
            }

            System.out.println(JSON.toJSONString(vo));

            try {
                caseMapper.insert(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private PartyEntity parseText(String text, String name) {
        text = text.replace("，", ",");
        text = text.replace("。", ",");
        String[] split = text.split(",");
        PartyEntity party = new PartyEntity();
        party.setContent(text);
        for (String s : split) {
            if (s.contains(name)) {
                party.setName(name);
            }
            if (s.contains("男")) {
                if (!StringUtils.hasLength(party.getSex())) {
                    party.setSex("男");
                }

            }

            if (s.contains("女")) {
                if (!StringUtils.hasLength(party.getSex())) {
                    party.setSex("女");
                }

            }
            if (s.contains("年") && s.contains("月") && s.contains("日") && s.contains("生")) {
                party.setBirthday(s);
                try {
                    String str = party.getBirthday().substring(party.getBirthday().indexOf("年") - 4, party.getBirthday().indexOf("日"));
                    str = str.replace("年", "-");
                    str = str.replace("月", "-");
                    party.setAge(DateUtil.ageOfNow(DateUtil.parse(str)) + "");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            if (nations.contains(s)) {
                party.setNation(s);
            }
            if (s.contains("住")) {
                party.setAddress(s);
            }
            if (s.contains("文化") || s.contains("文盲")) {
                party.setEduLevel(s);
            }
            if (text.contains("原告")) {
                party.setType("原告");

            } else if (text.contains("被告")) {
                party.setType("被告");

            }
        }
        return party;
    }
}
