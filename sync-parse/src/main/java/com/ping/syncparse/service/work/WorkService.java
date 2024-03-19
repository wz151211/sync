package com.ping.syncparse.service.work;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.common.DwbmCode;
import com.ping.syncparse.entity.AreaEntity;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.service.AreaService;
import com.ping.syncparse.service.CrimeVO;
import com.ping.syncparse.service.criminal.CriminalMapper;
import com.ping.syncparse.service.criminal.CriminalResultMapper;
import com.ping.syncparse.service.criminal.CriminalResultVO;
import com.ping.syncparse.service.criminal.CriminalVO;
import com.sun.corba.se.spi.orbutil.threadpool.Work;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
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
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class WorkService {

    @Autowired
    private WorkMapper workMapper;

    @Autowired
    private WorkResultMapper resultMapper;

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
    private int pageSize = 10000;
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
                if ("cause.txt".equals(resource.getFilename())) {
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
            causeSet = causeList.stream().peek(c -> {
                if (c.getName().endsWith("罪")) {
                    c.setName(c.getName().substring(0, c.getName().length() - 1));
                }
            }).map(Dict::getName).collect(toSet());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    AtomicInteger count = new AtomicInteger();

    public void parse() {
        CRFLexicalAnalyzer analyzer = null;
        try {
            analyzer = new CRFLexicalAnalyzer();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Criteria criteria = Criteria.where("fact").regex("死");
        //Criteria criteria = Criteria.where("caseNo").is("（2017）豫03刑初12号");


        List<WorkEntity> entities = workMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        CRFLexicalAnalyzer finalAnalyzer = analyzer;
        entities.parallelStream().forEach(entity -> {
            WorkResultEntity vo = new WorkResultEntity();
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
            if (entity.getKeyword() != null && entity.getKeyword().size() > 0) {
                vo.setKeyword(entity.getKeyword().stream().map(Object::toString).collect(joining(",")));
            }
            if (entity.getLegalBasis() != null && entity.getLegalBasis().size() > 0) {
                for (int i = 0; i < entity.getLegalBasis().size(); i++) {
                }
                vo.setLegalBasis(entity.getLegalBasis().stream().map(c -> {
                    JSONObject aa = JSONObject.parseObject(JSON.toJSONString(c));
                    return aa.getString("fgmc") + aa.getString("tkx");
                }).collect(joining(",")));
            }
            try {
                vo.setRefereeDate(entity.getRefereeDate());
                // vo.setRefereeDate(DateUtil.offsetHour(vo.getRefereeDate(), 8));

            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Document parse = null;
            if (entity.getHtmlContent() != null && entity.getJsonContent() != null && entity.getJsonContent().size() > 0) {
                PartyEntity party = null;
                parse = Jsoup.parse(entity.getHtmlContent());
                String text1 = parse.text();
                if (StringUtils.hasLength(text1)) {
                    vo.setWords(text1.length());
                }

                vo.setJudgeContent(entity.getJsonContent().getString("s28"));

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
                        boolean isExist = false;
                        for (int i = 0; i < elements.size(); i++) {
                            Element element = elements.get(i);
                            String text = element.text();
                            if (text.contains("受理") || text.contains("审理") || text.contains("意见") || text.contains("法律效力") || text.contains("刑事判决") || text.contains("刑诉")) {
                                break;
                            }
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
                                    if (text.contains("受理") || text.contains("审理") || text.contains("意见") || text.contains("法律效力") || text.contains("刑事判决") || text.contains("刑诉")) {
                                        break;
                                    }
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
                                    if (text.contains("受理") || text.contains("审理") || text.contains("意见") || text.contains("法律效力") || text.contains("刑事判决") || text.contains("刑诉")) {
                                        break;
                                    }
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
                                if (text.contains("受理") || text.contains("审理") || text.contains("意见") || text.contains("法律效力") || text.contains("刑事判决") || text.contains("刑诉")) {
                                    break;
                                }
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
                                if (text.contains("受理") || text.contains("审理") || text.contains("意见") || text.contains("法律效力") || text.contains("刑事判决") || text.contains("刑诉")) {
                                    break;
                                }
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
                                if (text.contains("受理") || text.contains("审理") || text.contains("意见") || text.contains("法律效力") || text.contains("刑事判决") || text.contains("刑诉")) {
                                    break;
                                }
                                if (text.contains(name)) {
                                    party = parseText(text, name);
                                    vo.getParty().add(party);
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                    }
                }
                for (int i = 0; i < 20; i++) {
                    if (i > elements.size() - 1) {
                        continue;
                    }
                    Element element = elements.get(i);
                    String text = element.text();
                    String records = "";
                    if (StringUtils.hasLength(entity.getLitigationRecords())) {
                        try {
                            records = entity.getLitigationRecords().substring(0, 10);
                        } catch (Exception e) {
                            records = entity.getLitigationRecords();
                            //   e.printStackTrace();
                        }
                    }
                    if (text.contains(records) || text.contains("受理") || text.contains("审理") || text.contains("意见") || text.contains("法律效力") || text.contains("刑事判决") || text.contains("刑诉") || text.contains("审核")) {
                        break;
                    }
                    if (text.startsWith("被告人")) {
                        try {
                            party = parseText(text, null);
                            boolean exist = false;
                            for (PartyEntity partyEntity : vo.getParty()) {
                                if (StringUtils.hasLength(partyEntity.getName()) && partyEntity.getName().equals(party.getName())) {
                                    exist = true;
                                }
                            }
                            if (!exist) {
                                vo.getParty().add(party);
                            }
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            String records = entity.getLitigationRecords();

            if (StringUtils.hasLength(records)) {
                records = records.replace("。", "，");
                String[] split = records.split("，");
                for (int i = 0; i < split.length; i++) {
                    String comma = split[i];
                    String text = "";
                    if (i > 0 && (comma.contains("同日受理") || comma.contains("同日立案"))) {
                        text = split[i - 1] + "，" + comma;
                    } else {
                        text = comma;
                    }

                    if (text.contains("立案") || text.contains("受理")) {
                        log.info("立案信息：{}", text);
                        String registerCaseDate = "";
                        for (Term term : ToAnalysis.parse(text)) {
                            if (term.getRealName().contains("同") || term.getRealName().contains("当")) {
                                continue;

                            }
                            if (term.getRealName().contains("年") && !registerCaseDate.contains("年")) {
                                registerCaseDate = term.getRealName().replace("年", "-");
                            }
                            if (term.getRealName().contains("月") && !registerCaseDate.contains("月")) {
                                String temp = term.getRealName().replace("月", "-");
                                if (temp.length() == 2) {
                                    temp = "0" + temp;
                                }
                                registerCaseDate += temp;
                            }
                            if (term.getRealName().contains("日") && !registerCaseDate.contains("日")) {
                                String temp = term.getRealName().replace("日", "");
                                if (temp.length() == 1) {
                                    temp = "0" + temp;
                                }
                                registerCaseDate += temp;
                            }
                        }
                        if (StringUtils.isEmpty(vo.getRegisterCaseDate()) && StringUtils.hasLength(registerCaseDate)) {
                            vo.setRegisterCaseDate(registerCaseDate);
                            vo.setRegisterCaseDateContent(text);
                        }

                    }
                }
            }
            String fact = entity.getFact();
            if (StringUtils.isEmpty(fact)) {
                if ((StringUtils.isEmpty(fact) || (StringUtils.hasLength(fact) && fact.length() < 10)) && StringUtils.hasLength(entity.getHtmlContent())) {
                    String text = parse.text();
                    int index = text.indexOf("本院认为");
                    if (index > 0) {
                        text = text.substring(0, index);
                    }
                    fact = text;
                }
            }
            if (StringUtils.hasLength(fact)) {
                String[] sentences = fact.split("。");
                for (String sentence : sentences) {
                    sentence = sentence.replace(";", "，");
                    sentence = sentence.replace("；", "，");
                    String[] split = sentence.split("，");
                    for (int i = 0; i < split.length; i++) {
                        String comma = split[i];
                        comma = comma.replace("Ｏ", "0");
                        comma = comma.replace("ｌ", "0");
                        comma = comma.replace(" ", "");
                        comma = comma.replace("日前", "日");
                        comma = comma.replace("23月", "23个月");
           /*         String text = "";
                    if (i > 0) {
                        text = split[i - 1] + "，" + comma;
                    } else {
                        text = comma;
                    }*/

                        if (StringUtils.isEmpty(vo.getRegisterCaseDate()) && StringUtils.hasLength(entity.getFact())) {
                            if ((comma.contains("立案") || comma.contains("受理")) && comma.contains("年") && comma.contains("月")) {
                                log.info("立案信息：{}", comma);
                                String registerCaseDate = "";
                                for (Term term : ToAnalysis.parse(comma)) {
                                    if (term.getRealName().contains("同") || term.getRealName().contains("当")) {
                                        continue;

                                    }
                                    if (term.getRealName().contains("年") && !registerCaseDate.contains("年")) {
                                        registerCaseDate = term.getRealName().replace("年", "-");
                                    }
                                    if (term.getRealName().contains("月") && !registerCaseDate.contains("月")) {
                                        String temp = term.getRealName().replace("月", "-");
                                        if (temp.length() == 2) {
                                            temp = "0" + temp;
                                        }
                                        registerCaseDate += temp;
                                    }
                                    if (term.getRealName().contains("日") && !registerCaseDate.contains("日")) {
                                        String temp = term.getRealName().replace("日", "");
                                        if (temp.length() == 1) {
                                            temp = "0" + temp;
                                        }
                                        registerCaseDate += temp;
                                    }
                                }
                                if (StringUtils.isEmpty(vo.getRegisterCaseDate()) && StringUtils.hasLength(registerCaseDate)) {
                                    vo.setRegisterCaseDate(registerCaseDate);
                                    vo.setRegisterCaseDateContent(comma);
                                }
                            }
                        }
                    }
                }
            }

            String judgmentResult = entity.getJudgmentResult();
            if (StringUtils.hasLength(judgmentResult)) {
                judgmentResult = judgmentResult.replace("。", "；");
                judgmentResult = judgmentResult.replace(";", "；");
                String[] split = judgmentResult.split("；");
                for (String temp : split) {
                    if (temp.contains("受理费") || temp.contains("诉讼费")) {
                        if (StringUtils.isEmpty(vo.getHearingFees())) {
                            vo.setHearingFees(temp);
                        }
                    }
                    if (temp.contains("驳回") && temp.contains("全部诉讼请求")) {
                        vo.setJudgmentDesc("驳回全部诉讼请求");
                        vo.setJudgmentDescContent(temp);
                    }

                    if (temp.contains("驳回") && temp.contains("其他诉讼请求")) {
                        vo.setJudgmentDesc("驳回部分诉讼请求");
                        vo.setJudgmentDescContent(temp);
                    }
                }
            }


            try {
                for (PartyEntity entity1 : vo.getParty()) {
                    parseAddress(entity1);
                    parseIdCard(entity1);
                }
                resultMapper.insert(vo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private PartyEntity parseText(String text, String name) {
        text = text.replace("，", ",");
        text = text.replace("。", ",");
        //text = text.replace("：", ",");
        String[] split = text.split(",");
        PartyEntity party = new PartyEntity();
        party.setContent(text);
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
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
                        if (StringUtils.isEmpty(party.getName())) {
                            int start = s.indexOf("被告");
                            party.setName(s.substring(start + 2));
                        }
                    }

                    if (s.contains("被申请人")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            int index = s.indexOf("被申请人");
                            try {
                                if (s.contains("（") && s.contains("）")) {
                                    int start = s.indexOf("（");
                                    int end = s.indexOf("）");
                                    s = s.substring(0, start) + s.substring(end + 1);
                                }
                                party.setName(s.substring(index + 4));
                            } catch (Exception e) {
                                log.info("party={}", s);
                                e.printStackTrace();
                            }

                        }
                    }

                    if (s.contains("申请人")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            int index = s.indexOf("申请人");
                            try {
                                if (s.contains("（") && s.contains("）")) {
                                    int start = s.indexOf("（");
                                    int end = s.indexOf("）");
                                    s = s.substring(0, start) + s.substring(end + 1);
                                }
                                party.setName(s.substring(index + 3));
                            } catch (Exception e) {
                                log.info("party={}", s);
                                e.printStackTrace();
                            }

                        }
                    }

                    if (s.contains("原告")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            int start = s.indexOf("原告");
                            party.setName(s.substring(start + 2));
                        }
                    }


                } catch (Exception e) {
                    //  log.info("s={}", s);
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
            if (s.contains("岁") && StringUtils.isEmpty(party.getAge())) {
                party.setAge(s.replace("岁", ""));
            }
            if (s.contains("年龄") && StringUtils.isEmpty(party.getAge())) {
                party.setAge(s.replace("年龄", ""));
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
                    party.setAgeContent(str);
                    //   log.info("出生日期={}", str);
                    if (StringUtils.isEmpty(party.getAge())) {
                        party.setAge(DateUtil.ageOfNow(DateUtil.parse(str)) + "");
                    }

                } catch (Exception e) {
                    //  log.error("s={}", s);
                    //  e.printStackTrace();
                }

            }
            if (nations.contains(s)) {
                party.setNation(s);
            }


            if (StringUtils.isEmpty(party.getAddress())) {
                if (s.contains("住") && (!s.equals("住所地") && !s.equals("住所")) && (s.contains("省") || s.contains("自治区") || s.contains("兵团") || s.contains("市") || s.contains("盟") || s.contains("自治州") || s.contains("县") || s.contains("旗") || s.contains("区"))) {
                    party.setAddress(s);
                } else {
                    if (i + 1 < split.length) {
                        String next = split[i + 1];
                        if ((s.equals("住") || s.equals("住所地") || s.equals("住所")) && (next.contains("省") || next.contains("自治区") || next.contains("兵团") || next.contains("市") || next.contains("盟") || next.contains("自治州") || next.contains("县") || next.contains("旗") || next.contains("区"))) {
                            party.setAddress(s + next);
                        }
                    }

                }
            }

            if (StringUtils.isEmpty(party.getAddress())) {
                if ((s.contains("省") || s.contains("自治区") || s.contains("兵团")) && (s.contains("市") || s.contains("自治州") || s.contains("盟")) && (s.contains("县") || s.contains("区") || s.contains("旗"))) {
                    if (!s.contains("检察院") && !s.contains("法院") && !s.contains("公安局") && !s.contains("公司") && !s.contains("看守所") && !s.contains("羁押")) {
                        party.setAddress(s);
                    }

                }
            }

            if (StringUtils.isEmpty(party.getAddress())) {
                if ((s.contains("省") || s.contains("自治区") || s.contains("兵团")) || (s.contains("市") || s.contains("自治州") || s.contains("盟")) && (s.contains("县") || s.contains("区") || s.contains("旗"))) {
                    if (!s.contains("检察院") && !s.contains("法院") && !s.contains("公安局") && !s.contains("公司") && !s.contains("看守所") && !s.contains("羁押")) {
                        party.setAddress(s);
                    }
                }
            }
            if ((s.contains("省") || s.contains("市") || s.contains("县") || s.contains("区") || s.contains("盟") || s.contains("旗")) && s.contains("人")) {
                if (!s.contains("检察院") && !s.contains("法院") && !s.contains("公安局") && !s.contains("公司") && !s.contains("看守所") && !s.contains("羁押") && !s.contains("被告") && !s.contains("原告")) {
                    party.setAddress(s);
                }
            }
            if ((s.contains("户籍") || s.contains("籍贯")) && (s.contains("省") || s.contains("自治区") || s.contains("兵团") || s.contains("市") || s.contains("盟") || s.contains("自治州") || s.contains("县") || s.contains("旗") || s.contains("区"))) {
                party.setAddress(s);
            } else {
                if (i + 1 < split.length) {
                    String next = split[i + 1];
                    if ((s.equals("户籍") || s.equals("籍贯") || s.equals("户籍地") || s.equals("户籍所在地")) && (next.contains("省") || next.contains("自治区") || next.contains("兵团") || next.contains("市") || next.contains("盟") || next.contains("自治州") || next.contains("县") || next.contains("旗") || next.contains("区"))) {
                        party.setAddress(s + next);
                    }
                }
            }

            if (s.contains("文化") || s.contains("文盲") || s.contains("肄业") || s.contains("专科") || s.contains("本科") || s.contains("毕业") || s.contains("大学") || s.contains("研究生") || s.contains("硕士") || s.contains("博士") || s.contains("教授") || s.contains("院士")) {
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
            if (text.contains("反诉被告") || text.contains("被执行人") || text.contains("被申请人") || text.contains("被上诉人") || text.contains("被申诉人") || text.contains("被强制医疗人")) {
                party.setType("被告");
            } else if (text.contains("反诉原告") || text.contains("申请执行人") || text.contains("申请人") || text.contains("自诉人") || text.contains("再审申请人") || text.contains("申诉人") || text.contains("上诉人") || text.contains("申请机关")) {
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

            String idCard = "";
            if (i == 0) {
                idCard = s;
            } else {
                idCard = split[i - 1] + s;
            }

            if (idCard.contains("身份证号") || idCard.contains("身份号码")) {
                int index = idCard.indexOf("身份证号");
                if (index == -1) {
                    index = idCard.indexOf("身份号码");
                }
                String temp = idCard.substring(index + 4);
                temp = temp.replace("：", "");
                temp = temp.replace(":", "");
                temp = temp.replace("）", "");
                temp = temp.replace(")", "");
                temp = temp.replace("。", "");
                temp = temp.replace(".", "");
                temp = temp.replace("，", "");
                temp = temp.replace(",", "");
                temp = temp.replace("，", "");
                temp = temp.replace("码", "");
                temp = temp.replace("为", "");
                temp = temp.replace("汉族", "");
                // temp = temp.substring(0, 18);

                int end = -1;
                if (temp.length() != 18) {
                    if (temp.contains("*") && end == -1) {
                        end = temp.lastIndexOf("*");
                    }
                    if (temp.contains("X") && end == -1) {
                        end = temp.lastIndexOf("X");
                    }
                    if (temp.contains("×") && end == -1) {
                        end = temp.lastIndexOf("×");
                    }
                    if (temp.contains("户") && end == -1) {
                        end = temp.lastIndexOf("户");
                    }
                    if (temp.contains("汉") && end == -1) {
                        end = temp.lastIndexOf("汉");
                    }
                    if (temp.contains("曾") && end == -1) {
                        end = temp.lastIndexOf("曾");
                    }
                    if (temp.contains("因") && end == -1) {
                        end = temp.lastIndexOf("因");
                    }
                    if (end > -1) {
                        temp = temp.substring(0, end);
                    }
                }
                //   log.info("身份证号={}  ==  {}", temp, idCard);
                if (StringUtils.isEmpty(party.getIdCard()) && StringUtils.hasLength(temp) && !temp.contains("族")) {
                    party.setIdCard(temp);
                }
            }
            if (!StringUtils.hasLength(party.getFirsOffender())) {
                if (s.contains("刑满释放") || s.contains("因犯") || s.contains("曾因") || s.contains("又因") || s.contains("再因")) {
                    party.setFirsOffender("否");
                } else {
                    party.setFirsOffender("是");
                }
            }

        }
        return party;
    }

    private void parseAddress(PartyEntity party) {
        if (party == null || !StringUtils.hasText(party.getAddress())) {
            return;
        }
        String address = party.getAddress();
/*        address = address.replace("住所地", "");
        address = address.replace("住址", "");
        address = address.replace("所在地", "");
        address = address.replace("住所", "");
        address = address.replace("现住", "");
        address = address.replace("住", "");*/
        party.setProvince(null);
        party.setCity(null);
        party.setCounty(null);
        for (Term term : DicAnalysis.parse(address)) {
            if ((term.getRealName().contains("省") || term.getRealName().contains("自治区") || term.getRealName().contains("兵团")) && (!term.getRealName().contains("住所地") && !term.getRealName().equals("住所") && !term.getRealName().contains("住址") && !term.getRealName().contains("所在地") && !term.getRealName().contains("住") && !term.getRealName().contains("现住") && !term.getRealName().contains("户籍地"))) {
                if (StringUtils.isEmpty(party.getProvince())) {
                    party.setProvince(term.getRealName());
                }
                if (term.getRealName().equals("自治区")) {
                    Term temp = term;
                    while (temp != null && !temp.getRealName().equals("BEGIN")) {
                        if (provinceSimple.containsKey(temp.getRealName())) {
                            String province = provinceSimple.get(temp.getRealName());
                            party.setProvince(province);
                            break;
                        }
                        party.setProvince(temp.getRealName() + party.getProvince());
                        temp = temp.from();
                    }
                }
            }
            if (term.getRealName().contains("市") || term.getRealName().contains("盟") || term.getRealName().contains("自治州")) {
                if (term.getRealName().length() <= 1 || term.getRealName().contains("自治州")) {
                    StringBuilder city = new StringBuilder();
                    Term temp = term;
                    for (int i = 0; i < 10; i++) {
                        if (temp == null || temp.getRealName().equals("BEGIN")) {
                            continue;
                        }
                        String name = temp.getRealName();
                        if (name.contains("省") || name.contains("自治区") || name.contains("兵团") || name.contains("住所地") || name.equals("住所") || name.contains("住址") || name.contains("所在地") || name.contains("住") || name.contains("现住") || name.contains("户籍地")) {
                            break;
                        }
                        if (provinceSimple.containsKey(name)) {
                            String province = provinceSimple.get(name);
                            party.setProvince(province);
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
                if (term.getRealName().length() <= 2 || term.getRealName().equals("开发区") || term.getRealName().equals("工业区") || term.getRealName().equals("自治县")) {
                    StringBuilder county = new StringBuilder();
                    Term temp = term;
                    for (int i = 0; i < 10; i++) {
                        if (temp == null || temp.getRealName().equals("BEGIN")) {
                            continue;
                        }
                        String name = temp.getRealName();
                        if (name.contains("省") || name.contains("自治区") || name.contains("兵团") || name.contains("市") || name.contains("盟") || name.contains("自治州") || name.contains("住所地") || name.equals("住所") || name.contains("住址") || name.contains("所在地") || name.contains("住") || name.contains("现住") || name.contains("户籍地")) {
                            break;
                        }
                        if (provinceSimple.containsKey(name)) {
                            String province = provinceSimple.get(name);
                            party.setProvince(province);
                            break;
                        }
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
                if (StringUtils.isEmpty(party.getProvince()) || !entity.getProvince().equals(party.getProvince())) {
                    party.setProvince(entity.getProvince());
                }
                if (StringUtils.isEmpty(party.getCity()) || (StringUtils.hasLength(entity.getCity()) && !entity.getCity().equals(party.getCity()))) {
                    party.setCity(entity.getCity());
                }
                if (StringUtils.isEmpty(party.getCounty()) || (StringUtils.hasLength(entity.getCounty()) && !entity.getCounty().equals(party.getCounty()))) {
                    party.setCounty(entity.getCounty());
                }
            }
        }
    }

    private void parseIdCard(PartyEntity party) {
        List<AreaEntity> areas = new ArrayList<>();
        if (StringUtils.hasLength(party.getCity()) && StringUtils.hasLength(party.getCounty())) {
            AreaEntity entity = areaService.findCounty(party.getCity(), party.getCounty());
            if (entity != null) {
                areas.add(entity);
            }
        }
        if (StringUtils.hasLength(party.getCity()) && StringUtils.isEmpty(party.getCounty())) {
            if (StringUtils.hasLength(party.getAgeContent())) {
                List<AreaEntity> entities = areaService.findCityChild(party.getCity());
                areas.addAll(entities);
            } else {
                AreaEntity entity = areaService.findCity(party.getCity());
                if (entity != null) {
                    areas.add(entity);
                }
            }
        }
        if (StringUtils.hasLength(party.getProvince()) && StringUtils.isEmpty(party.getCity()) && StringUtils.isEmpty(party.getCounty())) {
            if (StringUtils.hasLength(party.getAgeContent())) {
                List<AreaEntity> entities = areaService.findProvinceChild(party.getProvince());
                areas.addAll(entities);
            } else {
                AreaEntity entity = areaService.findProvince(party.getProvince());
                if (entity != null) {
                    areas.add(entity);
                }
            }
        }

        String idCard = party.getIdCard();
        if (StringUtils.hasLength(idCard)) {
            idCard = idCard.replace("×", "X");
            idCard = idCard.replace("*", "X");
        }
        if (StringUtils.hasLength(idCard) && idCard.length() == 18 && !idCard.contains("X")) {
            party.getIdCards().add(idCard);
        } else {
            for (AreaEntity area : areas) {
                if (area.getLevel() == 3 && StringUtils.hasLength(party.getAgeContent())) {
                    String code = area.getId().substring(0, 6);
                    String ageContent = party.getAgeContent();
                    String birthday = "";
                    ageContent = ageContent.replace("×", "X");
                    ageContent = ageContent.replace("*", "X");
                    String[] split = ageContent.split("-");
                    if (split.length == 3) {
                        for (int i = 1; i < split.length; i++) {
                            String s = split[i];
                            try {
                                if (s.contains("X")) {
                                    continue;
                                }
                                int num = Integer.parseInt(s);
                                if (num < 10) {
                                    split[i] = "0" + s;
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }

                        }
                        if (!split[0].contains("X")) {
                            birthday += split[0];

                        }
                        if (!split[1].contains("X")) {
                            birthday += split[1];

                        }
                        if (!split[2].contains("X")) {
                            birthday += split[2];

                        }
                    }
                    String prefix = code + birthday;
                    party.getIdCards().add(prefix);
                } else {
                    party.getIdCards().add(area.getId().substring(0, 6));
                }
                if (area.getLevel() == 2) {
                    party.getIdCards().add(area.getId().substring(0, 4));
                }
                if (area.getLevel() == 1) {
                    party.getIdCards().add(area.getId().substring(0, 2));
                }
            }

        }

    }

    private List<String> moneyList = new ArrayList<>();
    private List<String> provinceList = new ArrayList<>();
    private Map<String, String> province = new HashMap<>();
    private Map<String, String> provinceSimple = new HashMap<>();

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
        province.put("内蒙", "内蒙古自治区");
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

        provinceSimple.put("北京", "北京市");
        provinceSimple.put("天津", "天津市");
        provinceSimple.put("河北", "河北省");
        provinceSimple.put("山西", "山西省");
        provinceSimple.put("内蒙古", "内蒙古自治区");
        provinceSimple.put("内蒙", "内蒙古自治区");
        provinceSimple.put("辽宁", "辽宁省");
        provinceSimple.put("吉林", "吉林省");
        provinceSimple.put("黑龙江", "黑龙江省");
        provinceSimple.put("上海", "上海市");
        provinceSimple.put("江苏", "江苏省");
        provinceSimple.put("浙江", "浙江省");
        provinceSimple.put("安徽", "安徽省");
        provinceSimple.put("福建", "福建省");
        provinceSimple.put("江西", "江西省");
        provinceSimple.put("山东", "山东省");
        provinceSimple.put("河南", "河南省");
        provinceSimple.put("湖北", "湖北省");
        provinceSimple.put("湖南", "湖南省");
        provinceSimple.put("广东", "广东省");
        provinceSimple.put("广西", "广西壮族自治区");
        provinceSimple.put("海南", "海南省");
        provinceSimple.put("重庆", "重庆市");
        provinceSimple.put("四川", "四川省");
        provinceSimple.put("贵州", "贵州省");
        provinceSimple.put("云南", "云南省");
        provinceSimple.put("西藏", "西藏自治区");
        provinceSimple.put("陕西", "陕西省");
        provinceSimple.put("甘肃", "甘肃省");
        provinceSimple.put("青海", "青海省");
        provinceSimple.put("宁夏", "宁夏回族自治区");

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
        professionSet.add("营销中心副区长");
        professionSet.add("个体经营者");
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
        professionSet.add("美发师");


    }


    public void address(WorkEntity entity, WorkResultEntity vo) {
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

