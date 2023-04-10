package com.ping.syncparse.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.common.DwbmCode;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.sync.c34.DocumentMsMapper;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Nature;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.domain.TermNatures;
import org.ansj.splitWord.analysis.ToAnalysis;
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
    @Autowired
    private TempMapper tempMapper;

    private AtomicInteger pageNum = new AtomicInteger(0);

    private List<Dict> causeList = new ArrayList<>();
    private List<Dict> areaList = new ArrayList<>();
    Map<String, Dict> areaMap = new HashMap<>();
    Map<String, Dict> areaCodeMap = new HashMap<>();
    private Set<String> causeSet = new HashSet<>();


    private String[] temp = {"汉族", "满族", "回族", "藏族", "苗族", "彝族", "壮族", "侗族", "瑶族", "白族", "傣族", "黎族", "佤族", "畲族", "水族", "土族", "蒙古族", "布依族", "土家族", "哈尼族", "傈僳族", "高山族", "拉祜族", "东乡族", "纳西族", "景颇族", "哈萨克族", "维吾尔族", "达斡尔族", "柯尔克孜族", "羌族", "怒族", "京族", "德昂族", "保安族", "裕固族", "仫佬族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "朝鲜族", "赫哲族", "门巴族", "珞巴族", "独龙族", "基诺族", "塔吉克族", "俄罗斯族", "鄂温克族", "塔塔尔族", "鄂伦春族", "乌孜别克族"};

    private Set<String> eduLevel = new HashSet<>();
    private int pageSize = 1000;
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

    public void parse() {
        DateTime dateTime = DateUtil.parse("2019-01-01");
        Criteria criteria = Criteria.where("refereeDate").gt(dateTime.toJdkDate()).and("docType").is("判决书");
        List<DocumentXsLhEntity> entities = documentXsMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        for (DocumentXsLhEntity entity : entities) {
            CaseVo vo = new CaseVo();
            vo.setId(entity.getId());
            vo.setName(entity.getName());
            vo.setCaseNo(entity.getCaseNo());
            vo.setCourtName(entity.getCourtName());
            vo.setHtmlContent(entity.getHtmlContent());
            vo.setJsonContent(entity.getJsonContent());
            vo.setCaseType(entity.getCaseType());
            vo.setDocType(entity.getDocType());
            vo.setTrialProceedings(entity.getTrialProceedings());
            vo.setProvince(entity.getProvince());
            vo.setCity(entity.getCity());
            vo.setCounty(entity.getCounty());
            vo.setCause(entity.getCause().stream().map(Object::toString).collect(joining(",")));
            JSONObject jsonObject = entity.getJsonContent();
            String s25 = jsonObject.getString("s25");
            try {
                vo.setRefereeDate(entity.getRefereeDate());
                // vo.setRefereeDate(DateUtil.offsetHour(vo.getRefereeDate(), 8));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (entity.getHtmlContent() != null && entity.getJsonContent() != null && entity.getJsonContent().size() > 0) {
                PartyEntity party = null;
                JSONObject object = entity.getJsonContent();
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

                vo.setMoneySet(parseMoney(party, s25));

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
                try {
                    caseMapper.insert(vo);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                    e.printStackTrace();
                }

            }
            if (nations.contains(s)) {
                party.setNation(s);
            }

            if (!StringUtils.hasText(party.getAddress())) {
                if (!StringUtils.hasText(party.getAddress())) {
                    if (s.contains("户籍") || s.contains("籍贯") ) {
                        party.setAddress(s);
                    }
                }

                if (s.contains("住")) {
                    party.setAddress(s);
                }
                if (!StringUtils.hasText(party.getAddress())) {
                    if ((s.contains("省") || s.contains("市") || s.contains("县") || s.contains("区")) & s.contains("人")) {
                        if (!s.contains("出生") && !s.contains("检察院") && !s.contains("法院") && !s.contains("公安局")) {
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
            if (text.contains("原告")) {
                party.setType("原告");

            } else if (text.contains("被告")) {
                party.setType("被告");

            }
            if (!StringUtils.hasLength(party.getType())) {
                if (text.contains("公诉机关") && text.contains("检察院")) {
                    party.setType("原告");
                }
            }
            if (!StringUtils.hasLength(party.getType())) {
                if (text.contains("自诉人")) {
                    party.setType("原告");
                }
            }
            if (!StringUtils.hasLength(party.getType())) {
                if (text.contains("申请人")) {
                    party.setType("原告");
                }
            }

            if (!StringUtils.hasLength(party.getType())) {
                if (text.contains("被申请人")) {
                    party.setType("被告");
                }
            }
            if (!StringUtils.hasLength(party.getType())) {
                if (text.contains("被上诉人")) {
                    party.setType("被告");
                }
            }
            if (!StringUtils.hasLength(party.getProfession())) {
                for (String temp : professionSet) {
                    if (s.contains(temp)) {
                        party.setProfession(temp);
                    }
                }
            }
            if (!StringUtils.hasLength(party.getHasCriminalRecord())) {
                if (s.contains("刑满释放") || s.contains("因犯") || s.contains("曾因")) {
                    party.setHasCriminalRecord("是");
                }
            }
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

    private List<String> moneyList = new ArrayList<>();

    {
        moneyList.add("亿元");
        moneyList.add("万元");
        moneyList.add("元");
    }

    {
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
}