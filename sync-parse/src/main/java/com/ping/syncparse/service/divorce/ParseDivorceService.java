package com.ping.syncparse.service.divorce;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.common.DwbmCode;
import com.ping.syncparse.entity.AreaEntity;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.service.AreaService;
import com.ping.syncparse.service.TempMapper;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class ParseDivorceService {
    @Autowired
    private DocumentXsMapper documentXsMapper;
    @Autowired
    private DivorceMapper caseMapper;
    @Autowired
    private TempMapper tempMapper;
    @Autowired
    private AreaService areaService;

    private AtomicInteger pageNum = new AtomicInteger(0);

    private List<Dict> causeList = new ArrayList<>();
    private List<Dict> areaList = new ArrayList<>();
    Map<String, Dict> areaMap = new HashMap<>();
    Map<String, Dict> areaCodeMap = new HashMap<>();
    private Set<String> causeSet = new HashSet<>();
    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^(0|[1-9]\\d{0,11})\\.(\\d\\d)$"); // 不考虑分隔符的正确性
    private String[] temp = {"汉族", "满族", "回族", "藏族", "苗族", "彝族", "壮族", "侗族", "瑶族", "白族", "傣族", "黎族", "佤族", "畲族", "水族", "土族", "蒙古族", "布依族", "土家族", "哈尼族", "傈僳族", "高山族", "拉祜族", "东乡族", "纳西族", "景颇族", "哈萨克族", "维吾尔族", "达斡尔族", "柯尔克孜族", "羌族", "怒族", "京族", "德昂族", "保安族", "裕固族", "仫佬族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "朝鲜族", "赫哲族", "门巴族", "珞巴族", "独龙族", "基诺族", "塔吉克族", "俄罗斯族", "鄂温克族", "塔塔尔族", "鄂伦春族", "乌孜别克族"};

    private Set<String> eduLevel = new HashSet<>();
    private int pageSize = 30000;
    private Set<String> nations = new HashSet<>();

    private Set<String> professionSet = new HashSet<>();

    private List<Dict> areaNewList = new ArrayList<>();

    private Set<String> tempCode = new HashSet();


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
                    JSONArray array = JSON.parseArray(text);
                    for (Object o : array) {
                        JSONObject object = (JSONObject) o;
                        object.forEach((k, v) -> {
                            Dict dict = new Dict();
                            dict.setCode(k);
                            DwbmCode dwbmCode = DwbmCode.valueOf(k);
                            String prefix = dwbmCode.getPrefix();
                            if (prefix.length() == 4) {
                                dict.setPId(prefix.substring(0, 2) + "0000");
                            } else if (prefix.length() == 2) {
                                dict.setPId("-1");
                            } else if (prefix.length() == 6) {
                                if (prefix.startsWith("11") || prefix.startsWith("12") || prefix.startsWith("31") || prefix.startsWith("50")) {
                                    dict.setPId(prefix.substring(0, 2) + "0000");
                                } else {
                                    dict.setPId(prefix.substring(0, 4) + "00");
                                }

                            }
                            dict.setLevel(dwbmCode.getLevel().value());
                            dict.setName(v.toString());
                            areaList.add(dict);
                        });

                    }
                }
            }
            areaMap = areaList.stream().collect(toMap(Dict::getName, c -> c, (k1, k2) -> k1));
            areaCodeMap = areaList.parallelStream().collect(toMap(Dict::getCode, c -> c, (k1, k2) -> k2));
            causeSet = causeList.stream().map(Dict::getName).collect(toSet());


        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    AtomicInteger count = new AtomicInteger();

    public void parse() {
        List<DocumentXsLhEntity> entities = documentXsMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        entities.parallelStream().forEach(entity -> {
            DivorceVo vo = new DivorceVo();
            vo.setId(entity.getId());
            vo.setTId(entity.getTId());
            vo.setName(entity.getName());
            vo.setCaseNo(entity.getCaseNo());
            vo.setCourtName(entity.getCourtName());
            vo.setHtmlContent(entity.getHtmlContent());
            vo.setJsonContent(entity.getJsonContent());
            vo.setCaseType(entity.getCaseType());
            vo.setDocType(entity.getDocType());
            vo.setTrialProceedings(entity.getTrialProceedings());
            vo.setProvince(entity.getProvince());
            if (!StringUtils.hasLength(vo.getProvince())) {
                address(entity, vo);
            }
            if (StringUtils.hasLength(entity.getCity())) {
                vo.setCity(entity.getCity());
            }
            if (StringUtils.hasLength(vo.getCity())) {
                if (vo.getCity().contains("知识产权法院")) {
                    String city = vo.getCity();
                    int index = city.indexOf("知识产权法院");
                    vo.setCity(city.substring(0, index) + "市");
                }
                if (vo.getCity().contains("第一") || vo.getCity().contains("第二") || vo.getCity().contains("第三") || vo.getCity().contains("第四")) {
                    if (vo.getProvince().contains("北京") || vo.getProvince().contains("上海") || vo.getProvince().contains("天津") || vo.getProvince().contains("重庆")) {
                        vo.setCity(vo.getProvince());
                    } else {
                        vo.setCity("");
                    }
                }
            }
            if (StringUtils.hasLength(entity.getCounty())) {
                vo.setCounty(entity.getCounty());
            }

            if (StringUtils.hasLength(vo.getCounty())) {
                if (vo.getCounty().contains("第一") || vo.getCounty().contains("第二") || vo.getCounty().contains("第三") || vo.getCounty().contains("第四") || vo.getCounty().contains("互联网法院")) {
                    vo.setCounty("");
                }
            }

            vo.setJudgmentResult(entity.getJudgmentResult());
            vo.setCourtConsidered(entity.getCourtConsidered());
            vo.setLitigationRecords(entity.getLitigationRecords());
            vo.setFact(entity.getFact());
            if (entity.getCause() != null && entity.getCause().size() > 0) {
                vo.setCause(entity.getCause().stream().map(Object::toString).collect(joining(",")));
            }
            if (entity.getLegalBasis() != null && entity.getLegalBasis().size() > 0) {
                for (int i = 0; i < entity.getLegalBasis().size(); i++) {
                }
                vo.setLegalBasis(entity.getLegalBasis().stream().map(c -> {
                    JSONObject aa = JSONObject.parseObject(JSON.toJSONString(c));
                    return aa.getString("fgmc") + aa.getString("tkx");
                }).collect(joining(",")));
            }
            vo.setLegalBasisCount(entity.getLegalBasis().size() + "");
/*            if ("民事一审".equals(entity.getTrialProceedings())) {
                List<DocumentMsJtblEntity> byCaseNo = documentMsMapper.find(vo.getCaseNo());
                for (DocumentMsJtblEntity lh : byCaseNo) {
                    DocumentXsLhEntity xsLhEntity = new DocumentXsLhEntity();
                    lh.setTId(entity.getId());
                    BeanUtils.copyProperties(lh, xsLhEntity);
                    documentXsMapper.insert(xsLhEntity);
                }
            }*/
            try {
                vo.setRefereeDate(entity.getRefereeDate());
                // vo.setRefereeDate(DateUtil.offsetHour(vo.getRefereeDate(), 8));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (entity.getHtmlContent() != null && entity.getJsonContent() != null && entity.getJsonContent().size() > 0) {
                PartyEntity party = null;
                Document parse = Jsoup.parse(entity.getHtmlContent());

                Elements elements = new Elements();
                Elements trs = parse.getElementsByTag("tr");
                Elements div = parse.getElementsByTag("div");
                if (div.size() > 0) {
                    div.remove(0);
                }
                Elements p = parse.getElementsByTag("p");
                elements.addAll(trs);
                elements.addAll(div);
                elements.addAll(p);

                JSONArray array = entity.getParty();
                if (array != null && array.size() > 0) {
                    for (Object o : array) {
                        if (o.toString().contains("检察院")) {
                            continue;

                        }
                        boolean isExist = false;
                        for (int i = 0; i < elements.size(); i++) {
                            Element element = elements.get(i);
                            String text = element.text();
                            if (text.contains(o.toString())) {
                                party = parseText(text, o.toString());
                                vo.getParty().add(party);
                                isExist = true;
                                break;
                            }
                        }

                        if (!isExist) {
                            String name = "";
                            String s = o.toString();
                            if (s.length() == 2) {
                                name = s.substring(0, 1) + "某";
                                for (int i = 0; i < elements.size(); i++) {
                                    Element element = elements.get(i);
                                    String text = element.text();
                                    if (text.contains(name)) {
                                        party = parseText(text, name);
                                        vo.getParty().add(party);
                                        isExist = true;
                                        break;
                                    }
                                }
                            }

                        }

                        if (!isExist) {
                            String name = "";
                            String s = o.toString();
                            if (s.length() == 2) {
                                name = s.substring(0, 1) + "*";
                                for (int i = 0; i < elements.size(); i++) {
                                    Element element = elements.get(i);
                                    String text = element.text();
                                    if (text.contains(name)) {
                                        party = parseText(text, name);
                                        vo.getParty().add(party);
                                        isExist = true;
                                        break;
                                    }
                                }
                            }

                        }


                        if (!isExist) {
                            String name = "";
                            String s = o.toString();
                            if (s.length() == 3) {
                                name = s.substring(0, 1) + "某" + s.substring(2);
                            }
                            for (int i = 0; i < elements.size(); i++) {
                                Element element = elements.get(i);
                                String text = element.text();
                                if (text.contains(name)) {
                                    party = parseText(text, name);
                                    vo.getParty().add(party);
                                    isExist = true;
                                    break;
                                }
                            }

                        }
                        if (!isExist) {
                            String name = "";
                            String s = o.toString();
                            if (s.length() == 3) {
                                name = s.substring(0, 1) + "某某";
                            }
                            for (int i = 0; i < elements.size(); i++) {
                                Element element = elements.get(i);
                                String text = element.text();
                                if (text.contains(name)) {
                                    party = parseText(text, name);
                                    vo.getParty().add(party);
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                        if (!isExist) {
                            String name = "";
                            String s = o.toString();
                            if (s.length() == 3) {
                                name = s.substring(0, 1) + "**";
                            }
                            for (int i = 0; i < elements.size(); i++) {
                                Element element = elements.get(i);
                                String text = element.text();
                                if (text.contains(name)) {
                                    party = parseText(text, name);
                                    vo.getParty().add(party);
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < elements.size(); i++) {
                        Element element = elements.get(i);
                        String text = element.text();
                        if (text.contains("被告")) {
                            System.out.println(text);
                        }
                    }
                }
            }

            if ("民事案件".equals(entity.getCaseType())) {
                String fact = entity.getFact();
                if (StringUtils.hasLength(fact)) {
                    fact = fact.replace("；", "，");
                    fact = fact.replace("：", "，");
                    fact = fact.replace(",", "，");
                    fact = fact.replace("份", "");
                    String[] sentences = fact.split("。");
                    for (String sentence : sentences) {
                        try {
                            if (sentence.contains("（") && sentence.contains("）")) {
                                int start = sentence.indexOf("（");
                                int end = sentence.indexOf("）");
                                sentence = sentence.substring(0, start) + sentence.substring(end);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        String[] split = sentence.split("，");
                        for (int i = 0; i < split.length; i++) {
                            String temp = split[i];
                            if (temp.contains("介绍") && StringUtils.isEmpty(vo.getKnowWay())) {
                                if (temp.contains("媒人介绍") || (temp.contains("媒人") && temp.contains("介绍"))) {
                                    vo.setKnowWay("媒人介绍");
                                    vo.setKnowWayContent(temp);
                                } else if (temp.contains("经人介绍")) {
                                    vo.setKnowWay("经人介绍");
                                    vo.setKnowWayContent(temp);
                                } else if (temp.contains("网上相识")) {
                                    vo.setKnowWay("网上相识");
                                    vo.setKnowWayContent(temp);
                                } else if (temp.contains("同事")) {
                                    vo.setKnowWay("同事");
                                    vo.setKnowWayContent(temp);
                                } else if (temp.contains("公司认识")) {
                                    vo.setKnowWay("同事");
                                    vo.setKnowWayContent(temp);
                                } else if ((temp.contains("经") || temp.contains("通过")) && (temp.contains("相识") || temp.contains("认识"))) {
                                    int start = temp.indexOf("经");
                                    if (start == -1) {
                                        start = temp.indexOf("通过");
                                    }
                                    int end = temp.indexOf("相识");
                                    if (end == -1) {
                                        end = temp.indexOf("认识");
                                    }
                                    if (end > start) {
                                        try {
                                            vo.setKnowWay(temp.substring(start, end + 2));
                                        } catch (Exception e) {
                                            log.info("认识方式={}", temp);
                                            e.printStackTrace();
                                        }
                                        vo.setKnowWayContent(temp);
                                    }
                                }
                              /*  String knowDate = "";
                                String t = "";
                                if ((temp.contains("年") && temp.contains("月")) || (temp.contains("月") && temp.contains("日"))) {
                                    t = temp;
                                } else {
                                    if (i > 0) {
                                        t = split[i - 1] + temp;
                                    }
                                }
                                for (Term term : ToAnalysis.parse(t)) {
                                    if (knowDate.contains("年") && term.getRealName().contains("年")) {
                                        break;
                                    }
                                    if (knowDate.contains("月") && term.getRealName().contains("月")) {
                                        break;
                                    }
                                    if (term.getNatureStr().equals("t") && (temp.contains("年") || temp.contains("月") || temp.contains("日"))) {
                                        knowDate += term.getRealName();
                                    }

                                }
                                if (StringUtils.hasLength(knowDate) && StringUtils.isEmpty(vo.getKnowDate())) {
                                    vo.setKnowDate(knowDate);
                                    vo.setKnowDateContent(sentence);
                                }*/
                            }
                            temp = temp.replace("月底", "月");
                            temp = temp.replace("月经", "月");
                            temp = temp.replace("年末", "年");
                            if (StringUtils.isEmpty(vo.getKnowDate())) {
                                if (temp.contains("于") && ((temp.contains("年") && temp.contains("月")) || (temp.contains("月") && temp.contains("日"))) && (temp.contains("相识") || temp.contains("认识"))) {
                                    String knowDate = "";
                                    if (temp.contains("×")) {
                                        int year = temp.indexOf("年");
                                        int month = temp.indexOf("月");
                                        if (year >= 4 && month == -1) {
                                            knowDate = temp.substring(year - 4, year + 1);
                                        }
                                    } else {
                                        for (Term term : ToAnalysis.parse(temp)) {
                                            if (knowDate.contains("年") && term.getRealName().contains("年")) {
                                                break;
                                            }
                                            if (knowDate.contains("月") && term.getRealName().contains("月")) {
                                                break;
                                            }
                                            if (term.getNatureStr().equals("m")) {
                                                Term to = term.to();
                                                if (term.getRealName().length() == 4 && to != null && (to.getRealName().contains("农历") || to.getRealName().contains("月")))
                                                    knowDate += term.getRealName() + "年";
                                            }
                                            if (term.getNatureStr().equals("t") && (temp.contains("年") || temp.contains("月") || temp.contains("日"))) {
                                                knowDate += term.getRealName();
                                            }
                                        }
                                    }

                                    if (StringUtils.hasLength(knowDate)) {
                                        vo.setKnowDate(knowDate);
                                        vo.setKnowDateContent(sentence);
                                    }
                                }
                            }
                            if (StringUtils.isEmpty(vo.getKnowDate())) {
                                if (temp.contains("认识") || temp.contains("相识")) {
                                    String knowDate = "";
                                    String t = "";
                                    if (i > 0) {
                                        t = split[i - 1] + temp;
                                    }
                                    if ((t.contains("年") && t.contains("月")) || (t.contains("月") && t.contains("日"))) {
                                        for (Term term : ToAnalysis.parse(t)) {
                                            if (knowDate.contains("年") && term.getRealName().contains("年")) {
                                                break;
                                            }
                                            if (knowDate.contains("月") && term.getRealName().contains("月")) {
                                                break;
                                            }
                                            if (term.getNatureStr().equals("m")) {
                                                Term to = term.to();
                                                if (term.getRealName().length() == 4 && to != null && (to.getRealName().contains("农历") || to.getRealName().contains("月")))
                                                    knowDate += term.getRealName() + "年";
                                            }
                                            if (term.getNatureStr().equals("t") && (temp.contains("年") || temp.contains("月") || temp.contains("日"))) {
                                                knowDate += term.getRealName();
                                            }

                                        }
                                    }

                                    if (StringUtils.hasLength(knowDate)) {
                                        vo.setKnowDate(knowDate);
                                        vo.setKnowDateContent(sentence);
                                    }
                                }
                            }
                            if (StringUtils.isEmpty(vo.getKnowDate())) {
                                if (temp.contains("××××年××月") && (temp.contains("相识") || temp.contains("认识"))) {
                                    vo.setKnowDate("××××年××月");
                                    vo.setKnowDateContent(sentence);
                                }
                            }
                            if (StringUtils.isEmpty(vo.getEngagedDate())) {
                                if (temp.contains("订婚") || temp.contains("定亲") || temp.contains("建立婚约") || temp.contains("确立婚约") || (temp.contains("确定") && temp.contains("婚约关系"))) {
                                    vo.setEngaged("是");
                                    vo.setEngagedContent(sentence);
                                    if ((temp.contains("年") && temp.contains("月")) || (temp.contains("月") && temp.contains("日"))) {
                                        String engagedDate = "";
                                        for (Term term : ToAnalysis.parse(temp)) {
                                            if (term.getNatureStr().equals("t")) {
                                                engagedDate += term.getRealName();
                                            }
                                        }
                                        vo.setEngagedDate(engagedDate);
                                        vo.setEngagedDateContent(sentence);
                                    }
                                } else {
                                    vo.setEngaged("否");
                                }
                            }

                            if (StringUtils.isEmpty(vo.getLiveTogether())) {
                                if ((temp.contains("同居期间") || temp.contains("同居生活") || temp.contains("共同生活")) && (!temp.contains("未") || !temp.contains("没有") || !temp.contains("拒绝"))) {
                                    vo.setLiveTogether("是");
                                    vo.setLiveTogetherContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getDissolveRelationshipDate())) {
                                if (temp.contains("分手") || temp.contains("解除") || temp.contains("分居") || temp.contains("分开") || temp.contains("感情破裂") || temp.contains("结束同居生活")) {
                                    String dissolveRelationshipDate = "";
                                    String t = "";
                                    if ((temp.contains("年") && temp.contains("月")) || (temp.contains("月") && temp.contains("日"))) {
                                        t = temp;
                                    } else {
                                        if (i > 0) {
                                            t = split[i - 1];
                                        }
                                    }
                                    for (Term term : ToAnalysis.parse(t)) {
                                        if (term.getNatureStr().equals("t")) {
                                            dissolveRelationshipDate += term.getRealName();
                                        }
                                    }
                                    if (StringUtils.hasLength(dissolveRelationshipDate)) {
                                        vo.setDissolveRelationshipDate(dissolveRelationshipDate);
                                        vo.setDissolveRelationshipDateContent(sentence);
                                    }

                                }
                            }

                            if ((temp.contains("结婚登记") || temp.contains("登记结婚"))
                                    && (temp.contains("没有") || temp.contains("未") || temp.contains("没") || temp.contains("尽量")
                                    || temp.contains("不") || temp.contains("无法") || temp.contains("要求")
                                    || temp.contains("商量") || temp.contains("协商") || temp.contains("拒绝") || temp.contains("准备")
                            )) {
                                vo.setMarriageRegistration("否");
                                vo.setMarriageRegistrationContent(sentence);
                            } else if ((temp.contains("结婚登记") || temp.contains("登记结婚")) && !temp.contains("曾")) {
                                String marriageRegistrationDate = "";
                                if ((temp.contains("年") && temp.contains("月")) || (temp.contains("月") && temp.contains("日"))) {

                                    for (Term term : ToAnalysis.parse(temp)) {
                                        if (term.getNatureStr().equals("t")) {
                                            marriageRegistrationDate += term.getRealName();
                                        }
                                    }
                                    if (StringUtils.isEmpty(marriageRegistrationDate)) {
                                        int length = 4;
                                        int start = temp.indexOf("年");
                                        if (start < 4) {
                                            length = 2;
                                        }
                                        int end = temp.indexOf("日");
                                        if (end == -1) {
                                            end = temp.indexOf("月");
                                        }
                                        if (end > start) {
                                            try {
                                                marriageRegistrationDate = temp.substring(start - length, end + 1);
                                            } catch (Exception e) {
                                                log.info("登记日期={}", temp);
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }


                                if (StringUtils.hasLength(marriageRegistrationDate)) {
                                    vo.setMarriageRegistrationDate(marriageRegistrationDate);
                                    vo.setMarriageRegistrationDateContent(temp);
                                    vo.setMarriageRegistration("是");
                                    vo.setMarriageRegistrationContent(sentence);
                                }
                                if (StringUtils.isEmpty(vo.getMarriageRegistration())) {
                                    vo.setMarriageRegistration("是");
                                    vo.setMarriageRegistrationContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getHostingWedding())) {
                                if (temp.contains("未举办婚礼") || temp.contains("没有举办婚礼") || (temp.contains("未") && temp.contains("举办婚礼"))) {
                                    vo.setHostingWedding("否");
                                    vo.setHostingWeddingContent(temp);
                                } else if (temp.contains("未举行婚礼") || temp.contains("没有举行婚礼") || (temp.contains("未") && temp.contains("举行婚礼"))) {
                                    vo.setHostingWedding("否");
                                    vo.setHostingWeddingContent(temp);
                                } else if ((temp.contains("年") || temp.contains("月") || temp.contains("日")) && (temp.contains("举办婚礼") || temp.contains("举行婚礼"))) {
                                    vo.setHostingWedding("是");
                                    vo.setHostingWeddingContent(temp);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getAbort())) {
                                if (temp.contains("流产") || temp.contains("引产") || temp.contains("流掉")) {
                                    vo.setAbort("是");
                                    vo.setAbortContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getChild())) {
                                if ((!temp.contains("未") && !temp.contains("不能") && !temp.contains("没有") && !temp.contains("无生育") && !temp.contains("未有生育")) && (temp.contains("生育") || temp.contains("生下") || temp.contains("育有"))) {
                                    vo.setChild("是");
                                    vo.setChildContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getBridePrice())) {
                                if (((temp.contains("彩礼") || temp.contains("聘金") || temp.contains("礼金") || temp.contains("见面礼")) && temp.contains("元"))) {
                                    vo.setBridePrice("是");
                                    vo.setBridePriceContent(sentence);
                                    if ((temp.contains("给付") || temp.contains("付给") || temp.contains("交给") || temp.contains("收") || temp.contains("支付") || temp.contains("共计彩礼")) && (!temp.contains("退还") || temp.contains("返还"))) {
                                        for (Term term : ToAnalysis.parse(temp)) {
                                            if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                                if (StringUtils.isEmpty(vo.getBridePriceTotal())) {
                                                    vo.setBridePriceTotal(term.getRealName());
                                                }
                                            }
                                        }
                                        vo.setBridePriceTotalContent(temp);
                                    }

                                }
                            }
                            if (StringUtils.isEmpty(vo.getBridePriceGold())) {
                                if (temp.contains("项链") || temp.contains("戒指") || temp.contains("钻石") || temp.contains("钻戒")
                                        || temp.contains("金750") || temp.contains("黄金") || temp.contains("耳饰") || temp.contains("首饰")
                                        || temp.contains("金镯子") || temp.contains("手镯") || temp.contains("足金") || temp.contains("猪牌")
                                        || temp.contains("吊坠") || temp.contains("挂坠") || temp.contains("金器")
                                        || temp.contains("耳环") || temp.contains("耳坠")) {
                                    vo.setBridePriceGold("是");
                                    if (StringUtils.hasLength(vo.getBridePriceGoldContent())) {
                                        vo.setBridePriceGoldContent(vo.getBridePriceGoldContent() + "，" + temp);
                                    } else {
                                        vo.setBridePriceGoldContent(sentence);
                                    }
                                }
                            }
                            if (StringUtils.isEmpty(vo.getBridePriceCar())) {
                                if (temp.contains("汽车") && (temp.contains("买") || temp.contains("购置") || temp.contains("首付"))) {
                                    vo.setBridePriceCar("是");
                                    vo.setBridePriceCarContent(sentence);
                                } else if (temp.contains("订车款") || temp.contains("购车款")) {
                                    vo.setBridePriceCar("是");
                                    vo.setBridePriceCarContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getBridePriceHouse())) {
                                if ((temp.contains("楼房") || temp.contains("房屋") || temp.contains("房子") || temp.contains("住房") || temp.contains("房产")) && (temp.contains("购买") || temp.contains("买") || temp.contains("首付"))) {
                                    vo.setBridePriceHouse("是");
                                    vo.setBridePriceHouseContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getBridePriceFrom())) {
                                if ((temp.contains("彩礼") || temp.contains("聘金") || temp.contains("礼金")) && temp.contains("来源")) {
                                    vo.setBridePriceFrom(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getBridePriceTo())) {
                                if (temp.contains("用") && (temp.contains("彩礼") || temp.contains("聘金") || temp.contains("礼金")) &&
                                        (temp.contains("买") || temp.contains("生活费"))) {
                                    vo.setBridePriceToContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getBridePricePoverty())) {
                                if (temp.contains("生活困难") || temp.contains("普通农村")) {
                                    vo.setBridePricePoverty("是");
                                    vo.setBridePricePovertyContent(sentence);
                                }
                            }

                            if (StringUtils.isEmpty(vo.getBridePriceIndebted())) {
                                if ((temp.contains("借款") || temp.contains("贷款") || temp.contains("举债")) && (!temp.contains("未") || !temp.contains("没有") || !temp.contains("无"))) {
                                    vo.setBridePriceIndebted("是");
                                    vo.setBridePriceIndebtedContent(temp);
                                }

                            }

                            if (StringUtils.isEmpty(vo.getBridePriceReturn())) {
                                if (StringUtils.hasLength(entity.getJudgmentResult())) {
                                    String judgmentResult = entity.getJudgmentResult();
                                    judgmentResult = judgmentResult.replace(";", "。");
                                    judgmentResult = judgmentResult.replace("；", "。");
                                    for (String result : judgmentResult.split("。")) {
                                        if (result.contains("维持原判")) {
                                            vo.setBridePriceReturn("维持原判");
                                            vo.setBridePriceReturnContent(result);
                                        } else if ((result.contains("退还") || result.contains("返还")) && result.contains("彩礼")) {
                                            for (Term term : ToAnalysis.parse(result)) {
                                                if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                                    if (StringUtils.isEmpty(vo.getBridePriceReturn())) {
                                                        vo.setBridePriceReturn(term.getRealName());
                                                    } else {
                                                        vo.setBridePriceReturn(vo.getBridePriceReturn() + "\r\n" + term.getRealName());
                                                    }
                                                    break;
                                                }
                                            }
                                            if (StringUtils.isEmpty(vo.getBridePriceReturnContent())) {
                                                vo.setBridePriceReturnContent(result);
                                            } else {
                                                vo.setBridePriceReturnContent(vo.getBridePriceReturnContent() + "\r\n" + result);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            try {
                for (PartyEntity entity1 : vo.getParty()) {
                    parseAddress(entity1);
                }
                caseMapper.insert(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private PartyEntity parseText(String text, String name) {
        text = text.replace("，", ",");
        text = text.replace("。", ",");
        text = text.replace("：", ",");
        String[] split = text.split(",");
        PartyEntity party = new PartyEntity();
        party.setContent(text);
        for (String s : split) {
            if (StringUtils.hasLength(name) && s.contains(name)) {
                party.setName(name);
            }
            if (!StringUtils.hasLength(party.getName())) {
                try {
                    if (s.contains("被告人")) {
                        int start = s.indexOf("被告人");
                        party.setName(s.substring(start + 3));
                    }
                    if (s.contains("被告")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            int start = s.indexOf("被告");
                            party.setName(s.substring(start + 2));
                        }
                    }

                    if (s.contains("原告")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            int start = s.indexOf("原告");
                            party.setName(s.substring(start + 2));
                        }
                    }


                } catch (Exception e) {
                    log.info("s={}", s);
                    e.printStackTrace();
                }
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
                    int start = party.getBirthday().indexOf("年") - 4;
                    int end = party.getBirthday().indexOf("日");
                    if (end < start) {
                        end = party.getBirthday().lastIndexOf("日");
                    }
                    String str = party.getBirthday().substring(start, end);
                    str = str.replace("年", "-");
                    str = str.replace("月", "-");
                    str = str.replace(" ", "");
                    party.setAge(DateUtil.ageOfNow(DateUtil.parse(str)) + "");
                } catch (Exception e) {
                    log.error("s={}", s);
                    //  e.printStackTrace();
                }

            }
            if (nations.contains(s)) {
                party.setNation(s);
            }


            if (!StringUtils.hasText(party.getAddress())) {
                if (s.contains("户籍") || s.contains("籍贯")) {
                    party.setAddress(s);
                }

                if (!StringUtils.hasText(party.getAddress())) {
                    if (s.contains("住") && !s.equals("住所地")) {
                        party.setAddress(s);
                    }
                }

                if (!StringUtils.hasText(party.getAddress())) {
                    if ((s.contains("省") || s.contains("市") || s.contains("县") || s.contains("区")) & s.contains("人")) {
                        if (!s.contains("出生") && !s.contains("检察院") && !s.contains("法院") && !s.contains("公安局") && !s.contains("公司")) {
                            party.setAddress(s);
                        }
                    }
                }
                if (StringUtils.isEmpty(party.getAddress())) {
                    if ((s.contains("省") || s.contains("自治区") || s.contains("兵团"))
                            && (s.contains("市") || s.contains("自治州") || s.contains("盟"))
                            && (s.contains("县") || s.contains("区") || s.contains("旗"))) {
                        if (!s.contains("公司") && !s.contains("委员会")) {
                            party.setAddress(s);
                        }

                    }
                }

                if (StringUtils.isEmpty(party.getAddress())) {
                    if ((s.contains("省") || s.contains("自治区") || s.contains("兵团"))
                            || (s.contains("市") || s.contains("自治州") || s.contains("盟"))
                            && (s.contains("县") || s.contains("区") || s.contains("旗"))) {
                        if (!s.contains("公司") && !s.contains("委员会")) {
                            party.setAddress(s);
                        }
                    }
                }

            }

            if (s.contains("文化")
                    || s.contains("文盲")
                    || s.contains("肄业")
                    || s.contains("专科")
                    || s.contains("本科")
                    || s.contains("毕业")
                    || s.contains("大学")
                    || s.contains("研究生")
                    || s.contains("硕士")
                    || s.contains("博士")
                    || s.contains("教授")
                    || s.contains("院士")) {
                if (s.length() < 9) {
                    if (!StringUtils.hasText(party.getEduLevel())) {
                        party.setEduLevel(s);
                    }
                }
                if (!StringUtils.hasText(party.getEduLevel())) {
                    for (String s1 : eduLevel) {
                        if (s1.contains(s) || s.contains(s1)) {
                            party.setEduLevel(s1);
                        }
                    }
                }
            }
            if (text.contains("反诉被告") || text.contains("被执行人") || text.contains("被申请人") || text.contains("被上诉人") || text.contains("被申诉人")) {
                party.setType("被告");
            } else if (text.contains("反诉原告") || text.contains("申请执行人") || text.contains("申请人") || text.contains("自诉人") || text.contains("再审申请人") || text.contains("申诉人") || text.contains("上诉人")) {
                party.setType("原告");
            } else if (text.contains("公诉机关") && text.contains("检察院")) {
                party.setType("原告");
            } else if (text.contains("原告")) {
                party.setType("原告");
            } else if (text.contains("被告")) {
                party.setType("被告");
            }

            if (text.contains("原审被告") && text.contains("上诉人")) {
                party.setType("被告");
            } else if (text.contains("原审原告") && text.contains("上诉人")) {
                party.setType("原告");
            }
            if (!StringUtils.hasLength(party.getProfession())) {
                for (String temp : professionSet) {
                    if (s.contains(temp)) {
                        party.setProfession(temp);
                    }
                }
            }
   /*         if (!StringUtils.hasLength(party.getHasCriminalRecord())) {
                if (s.contains("刑满释放") || s.contains("因犯") || s.contains("曾因")) {
                    party.setHasCriminalRecord("是");
                }
            }*/
        }
        return party;
    }

    private Set<String> parseMoney(PartyEntity party, String text) {
        if (!StringUtils.hasLength(text)) {
            return new HashSet<>();
        }
        Set<String> set = new HashSet<>();
        String content = text.replace("。", "，");
        content = content.replace("；", "，");
        content = content.replace(",", "，");
        for (String temp : content.split("，")) {
            Result parse = ToAnalysis.parse(temp);
            for (Term term : parse.getTerms()) {
                if (term.termNatures().numAttr.isNum()) {
                    if (term.getNatureStr().equals("mq") && term.getRealName().contains("元") && temp.contains("骗")) {
                        log.info("文本内容为:{}", temp);
                        log.info("金额内容为:{}", term.getRealName());
                        set.add(temp);
                    }
                }

            }
        }
        return set;
    }

    private void parseAddress(PartyEntity party) {
        if (party == null || !StringUtils.hasText(party.getAddress())) {
            return;
        }
        String address = party.getAddress();

        for (Term term : ToAnalysis.parse(address)) {
            if (term.getRealName().contains("省") || term.getRealName().contains("自治区") || term.getRealName().contains("兵团")) {
                if (StringUtils.isEmpty(party.getProvince())) {
                    party.setProvince(term.getRealName());
                }
            }
            if (term.getRealName().contains("市") || term.getRealName().contains("盟") || term.getRealName().contains("自治州")) {
                if (term.getRealName().length() <= 1) {
                    StringBuilder city = new StringBuilder();
                    Term temp = term;
                    for (int i = 0; i < 10; i++) {
                        if (temp == null || temp.getRealName().equals("BEGIN")) {
                            continue;
                        }
                        String name = temp.getRealName();
                        if (name.contains("省") || name.contains("自治区") || name.contains("兵团")) {
                            break;
                        }
                        city.insert(0, name);
                        temp = temp.from();
                    }
                    if (StringUtils.isEmpty(party.getCity())) {
                        party.setCity(city.toString());
                    }
                } else {
                    if (StringUtils.isEmpty(party.getCity())) {
                        party.setCity(term.getRealName());
                    }
                }
            }

            if (term.getRealName().contains("县") || term.getRealName().contains("旗") || (term.getRealName().contains("区") && !term.getRealName().contains("自治区"))) {
                if (term.getRealName().length() <= 2 || term.getRealName().equals("开发区") || term.getRealName().equals("工业区")) {
                    StringBuilder county = new StringBuilder();
                    Term temp = term;
                    for (int i = 0; i < 10; i++) {
                        if (temp == null || temp.getRealName().equals("BEGIN")) {
                            continue;
                        }
                        String name = temp.getRealName();
                        if (name.contains("省") || name.contains("自治区") || name.contains("兵团") || name.contains("市") || name.contains("盟") || name.contains("自治州") || name.contains("住所地") || name.contains("住")) {
                            break;
                        }
                        //    county.append(name).append("&");
                        county.insert(0, name);
                        temp = temp.from();
                    }
                    if (StringUtils.isEmpty(party.getCounty())) {
                        party.setCounty(county.toString());
                    }
                } else {
                    if (StringUtils.isEmpty(party.getCounty())) {
                        party.setCounty(term.getRealName());
                    }
                }
            }
            AreaEntity entity = areaService.find(party.getCity(), party.getCounty());
            if (entity != null) {
                if (StringUtils.isEmpty(party.getProvince())) {
                    party.setProvince(entity.getProvince());
                }
                if (StringUtils.isEmpty(party.getCity())) {
                    party.setCity(entity.getCity());
                }
                if (StringUtils.isEmpty(party.getCounty())) {
                    party.setCounty(entity.getCounty());
                }
            }
        }
    }

    private List<String> moneyList = new ArrayList<>();
    private List<String> provinceList = new ArrayList<>();
    private Map<String, String> province = new HashMap<>();

    private String convert(String name) {
        for (String s : province.keySet()) {
            if (StringUtils.hasLength(name) && name.contains(s)) {
                return province.get(s);
            }
        }
        return null;
    }

    {
        moneyList.add("亿元");
        moneyList.add("万元");
        moneyList.add("元");
    }

    {

        province.put("京", "北京市");
        province.put("津", "天津市");
        province.put("冀", "河北省");
        province.put("晋", "山西省");
        province.put("内", "内蒙古自治区");
        province.put("内蒙古", "内蒙古自治区");
        province.put("辽", "辽宁省");
        province.put("吉", "吉林省");
        province.put("黑", "黑龙江省");
        province.put("沪", "上海市");
        province.put("苏", "江苏省");
        province.put("浙", "浙江省");
        province.put("皖", "安徽省");
        province.put("闽", "福建省");
        province.put("赣", "江西省");
        province.put("鲁", "山东省");
        province.put("豫", "河南省");
        province.put("鄂", "湖北省");
        province.put("湘", "湖南省");
        province.put("粤", "广东省");
        province.put("桂", "广西壮族自治区");
        province.put("琼", "海南省");
        province.put("川", "四川省");
        province.put("蜀", "四川省");
        province.put("贵", "贵州省");
        province.put("黔", "贵州省");
        province.put("滇", "云南省");
        province.put("云", "云南省");
        province.put("渝", "重庆市");
        province.put("藏", "西藏自治区");
        province.put("秦", "陕西省");
        province.put("陕", "陕西省");
        province.put("甘", "甘肃省");
        province.put("陇", "甘肃省");
        province.put("青", "青海省");
        province.put("宁", "宁夏回族自治区");
        province.put("新", "新疆维吾尔自治区");
        provinceList.add("北京市");
        provinceList.add("天津市");
        provinceList.add("河北省");
        provinceList.add("山西省");
        provinceList.add("内蒙古自治区");
        provinceList.add("辽宁省");
        provinceList.add("吉林省");
        provinceList.add("黑龙江省");
        provinceList.add("上海市");
        provinceList.add("江苏省");
        provinceList.add("浙江省");
        provinceList.add("安徽省");
        provinceList.add("福建省");
        provinceList.add("江西省");
        provinceList.add("山东省");
        provinceList.add("河南省");
        provinceList.add("湖北省");
        provinceList.add("湖南省");
        provinceList.add("广东省");
        provinceList.add("广西壮族自治区");
        provinceList.add("海南省");
        provinceList.add("重庆市");
        provinceList.add("四川省");
        provinceList.add("贵州省");
        provinceList.add("云南省");
        provinceList.add("西藏自治区");
        provinceList.add("陕西省");
        provinceList.add("甘肃省");
        provinceList.add("青海省");
        provinceList.add("宁夏回族自治区");
        nations.addAll(Arrays.asList(temp));
        tempCode.add("110000");
        tempCode.add("120000");
        tempCode.add("310000");
        tempCode.add("500000");

        eduLevel.add("文盲");
        eduLevel.add("小学文化");
        eduLevel.add("初中文化");
        eduLevel.add("高中文化");
        eduLevel.add("本科");
        eduLevel.add("专科");
        eduLevel.add("小学肄业");
        eduLevel.add("高中毕业");
        eduLevel.add("小学毕业");
        eduLevel.add("初中毕业");
        eduLevel.add("本科毕业");
        eduLevel.add("专科毕业");
        eduLevel.add("专科文化");
        eduLevel.add("大学专科");
        eduLevel.add("大学专科文化");
        eduLevel.add("本科文化");
        eduLevel.add("大学本科文化");
        eduLevel.add("大学本科");
        eduLevel.add("中技文化程度");
        eduLevel.add("大学专科肄业文化");
        eduLevel.add("大学本科文化程度");

        professionSet.add("汽车质检员");
        professionSet.add("个体");
        professionSet.add("个体工商户");
        professionSet.add("个体营业");
        professionSet.add("无业");
        professionSet.add("无固定职业");
        professionSet.add("无职业");
        professionSet.add("务工");
        professionSet.add("零工");
        professionSet.add("打工");
        professionSet.add("工人");
        professionSet.add("务农");
        professionSet.add("务农人员");
        professionSet.add("务工人员");
        professionSet.add("农民");
        professionSet.add("农民工");
        professionSet.add("牧民");
        professionSet.add("经商");
        professionSet.add("无职业");
        professionSet.add("个体经营户");
        professionSet.add("居民");
        professionSet.add("司机");
        professionSet.add("村民");
        professionSet.add("员工");
        professionSet.add("业务员");
        professionSet.add("中心服务代表");
        professionSet.add("个体劳动者");
        professionSet.add("营销员");
        professionSet.add("销售总监");
        professionSet.add("职工");
        professionSet.add("销售");
        professionSet.add("总裁助理");
        professionSet.add("助理");
        professionSet.add("总监");
        professionSet.add("店长");
        professionSet.add("区域经理");
        professionSet.add("市场经理");
        professionSet.add("总经理");
        professionSet.add("团队经理");
        professionSet.add("业务经理");
        professionSet.add("副总经理");
        professionSet.add("总经理");
        professionSet.add("大区经理");
        professionSet.add("经理");
        professionSet.add("法定代表人");
        professionSet.add("地区负责人");
        professionSet.add("负责人");
        professionSet.add("监事");
        professionSet.add("公司控制人");
        professionSet.add("控制人");
        professionSet.add("实际经营人");
        professionSet.add("经营人");
        professionSet.add("讲师");
        professionSet.add("市级代理");
        professionSet.add("自由职业");
        professionSet.add("首席技术官");
        professionSet.add("投资人");
        professionSet.add("营销中心副区长");
        professionSet.add("经营者");
        professionSet.add("创始人");
        professionSet.add("首席运营官");
        professionSet.add("服务专员");
        professionSet.add("在职研究生文化");
        professionSet.add("党组书记");
        professionSet.add("主任");
        professionSet.add("执行董事");
        professionSet.add("客户主管");
        professionSet.add("主管");
        professionSet.add("支行长");
        professionSet.add("行长");
        professionSet.add("社员");
        professionSet.add("职员");
        professionSet.add("区级代理");
        professionSet.add("副校长");
        professionSet.add("校长");
        professionSet.add("社区秘书");
        professionSet.add("董事长");
        professionSet.add("代理商");


    }


    public void address(DocumentXsLhEntity entity, DivorceVo vo) {
        if (StringUtils.hasLength(entity.getCourtName())) {
            if (entity.getCourtName().contains("省")) {

                vo.setProvince(entity.getCourtName().substring(0, entity.getCourtName().indexOf("省") + 1));
                if (!StringUtils.hasLength(vo.getProvince())) {
                    vo.setProvince(convert(vo.getCaseNo()));
                }
                if (!StringUtils.hasLength(vo.getProvince())) {
                    vo.setProvince(convert(vo.getCaseNo()));
                }
            } else if (entity.getCourtName().contains("区")) {
                String s = entity.getCourtName().substring(0, entity.getCourtName().indexOf("区") + 1);
                Dict dict = areaMap.get(s.trim());
                if (dict != null) {
                    if (dict.getPId().equals("-1")) {
                        vo.setProvince(dict.getName());
                    } else {
                        Dict dict1 = areaCodeMap.get(dict.getPId());
                        if (dict1 != null && dict1.getPId().equals("-1")) {
                            vo.setProvince(dict1.getName());
                        } else {
                            if (dict1 != null) {
                                Dict dict2 = areaCodeMap.get(dict1.getPId());
                                if (dict2 != null && dict2.getPId().equals("-1")) {
                                    vo.setProvince(dict2.getName());
                                }
                            }

                        }
                    }
                }
                if (!StringUtils.hasLength(vo.getProvince())) {
                    vo.setProvince(convert(vo.getCaseNo()));
                }
            } else if (entity.getCourtName().contains("县")) {

                String s = entity.getCourtName().substring(0, entity.getCourtName().indexOf("县") + 1);

                Dict dict = areaMap.get(s.trim());
                if (dict != null) {
                    if (dict.getPId().equals("-1")) {
                        vo.setProvince(dict.getName());
                    } else {
                        Dict dict1 = areaCodeMap.get(dict.getPId());
                        if (dict1 != null && dict1.getPId().equals("-1")) {
                            vo.setProvince(dict1.getName());
                        } else {
                            if (dict1 != null) {
                                Dict dict2 = areaCodeMap.get(dict1.getPId());
                                if (dict2 != null && dict2.getPId().equals("-1")) {
                                    vo.setProvince(dict2.getName());
                                }
                            }

                        }
                    }
                }
                if (!StringUtils.hasLength(vo.getProvince())) {
                    vo.setProvince(convert(vo.getCaseNo()));
                }

            } else if (entity.getCourtName().contains("旗")) {
                String s = entity.getCourtName().substring(0, entity.getCourtName().indexOf("旗") + 1);

                Dict dict = areaMap.get(s.trim());
                if (dict != null) {
                    if (dict.getPId().equals("-1")) {
                        vo.setProvince(dict.getName());
                    } else {
                        Dict dict1 = areaCodeMap.get(dict.getPId());
                        if (dict1 != null && dict1.getPId().equals("-1")) {
                            vo.setProvince(dict1.getName());
                        } else {
                            if (dict1 != null) {
                                Dict dict2 = areaCodeMap.get(dict1.getPId());
                                if (dict2 != null && dict2.getPId().equals("-1")) {
                                    vo.setProvince(dict2.getName());
                                }
                            }

                        }
                    }
                }
                if (!StringUtils.hasLength(vo.getProvince())) {
                    vo.setProvince(convert(vo.getCaseNo()));
                }
            } else if (entity.getCourtName().contains("市")) {
                String s = entity.getCourtName().substring(0, entity.getCourtName().indexOf("市") + 1);
                Dict dict = areaMap.get(s.trim());
                if (dict != null) {
                    if (dict.getPId().equals("-1")) {
                        vo.setProvince(dict.getName());
                    } else {
                        Dict dict1 = areaCodeMap.get(dict.getPId());
                        if (dict1 != null && dict1.getPId().equals("-1")) {
                            vo.setProvince(dict1.getName());
                        } else {
                            if (dict1 != null) {
                                Dict dict2 = areaCodeMap.get(dict1.getPId());
                                if (dict2 != null && dict2.getPId().equals("-1")) {
                                    vo.setProvince(dict2.getName());
                                }
                            }
                        }
                    }
                }
                if (!StringUtils.hasLength(vo.getProvince())) {
                    vo.setProvince(convert(vo.getCaseNo()));
                }

            }
        }
        if (!StringUtils.hasLength(vo.getProvince())) {
            vo.setProvince(convert(vo.getCaseNo()));
        }
    }
}
