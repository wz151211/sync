package com.ping.syncparse.service.gamble;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.common.DwbmCode;
import com.ping.syncparse.entity.AreaEntity;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.service.AreaService;
import com.ping.syncparse.service.CaseMapper;
import com.ping.syncparse.service.CaseVo;
import com.ping.syncparse.service.TempMapper;
import com.ping.syncparse.sync.c34.DocumentMsMapper;
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
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

@Service
@Slf4j
public class ParseGambleService {
    @Autowired
    private DocumentMsMapper documentMsMapper;

    @Autowired
    private DocumentXsMapper documentXsMapper;
    @Autowired
    private CaseMapper caseMapper;
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


        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    AtomicInteger count = new AtomicInteger();

    public void parse() {
        Pattern compile = Pattern.compile("^((?!解).)*$", Pattern.CASE_INSENSITIVE);

        Criteria criteria = Criteria.where("caseNo").is(compile);
        //Criteria criteria = Criteria.where("caseNo").regex("解");

        List<DocumentXsLhEntity> entities = documentXsMapper.findList(pageNum.get(), pageSize, criteria);
        pageNum.getAndIncrement();
        entities.parallelStream().forEach(entity -> {
            CaseVo vo = new CaseVo();
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
                        if (o.toString().contains("检察院") || o.toString().contains("医院")) {
                            vo.setApplicant(o.toString());
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
                }
                for (int i = 0; i < 20; i++) {
                    if (i > elements.size() - 1) {
                        continue;
                    }
                    Element element = elements.get(i);
                    String text = element.text();
                    text = text.replace(",", "，");
                    if (text.contains("被申请人") || text.startsWith("申请机关") || text.contains("原申请机关") || text.contains("被强制医疗人")) {
                        //   log.info("当事人信息={}", text);
                        try {
                            party = parseText(text, null);
                            vo.getParty().add(party);
                            break;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                if ("刑事案件".equals(entity.getCaseType())) {
                    String fact = entity.getFact();
                    if (StringUtils.isEmpty(fact)) {
                        fact = entity.getCourtConsidered();
                    }
                    if (StringUtils.hasLength(fact)) {
                        fact = fact.replace(",", "，");
                        fact = fact.replace("；", "，");
                        fact = fact.replace("：", "，");
                        for (String paragraph : fact.split("。")) {
                            for (String sentence : paragraph.split("，")) {
                                if ((sentence.contains("合计")
                                        || sentence.contains("总计")
                                        || sentence.contains("共抽头获利")
                                        || sentence.contains("认定其盈利数额")
                                        || sentence.contains("共计抽头获利")
                                        || sentence.contains("共非法获利")
                                        || sentence.contains("赌场抽头获利")
                                        || sentence.contains("抽头渔利共计")
                                        || sentence.contains("抽头渔利")
                                        || sentence.contains("非法获利约")
                                        || sentence.contains("牟取非法收入")
                                        || sentence.contains("赔付玩家中奖的总金额共计")
                                        || sentence.contains("金额共计")
                                        || sentence.contains("赌资数额累计")
                                        || sentence.contains("下注金额")
                                        || sentence.contains("共获利")
                                        || sentence.contains("共计获利")
                                        || sentence.contains("投注额")
                                        || sentence.contains("赌注金额")
                                        || sentence.contains("累计盈利")
                                        || sentence.contains("牟利共计")
                                        || sentence.contains("至今获利")
                                        || sentence.contains("总金额")
                                        || sentence.contains("微信红包的总")
                                        || sentence.contains("“抽水”渔利共")
                                        || sentence.contains("抽取渔利")
                                        || sentence.contains("涉案金额")
                                        || sentence.contains("抽水获利")
                                        || sentence.contains("投注金额")
                                        || sentence.contains("共计抽取头薪")
                                        || sentence.contains("赌博金额")
                                        || sentence.contains("累计获利")
                                        || sentence.contains("赌场非法获利")
                                        || sentence.contains("非法牟利")
                                        || sentence.contains("收支总额")
                                        || sentence.contains("共抢到红包")
                                        || sentence.contains("微信群获利")
                                        || sentence.contains("赌场获利")) && sentence.contains("元")) {
                                    sentence = sentence.replace("余", "");
                                    sentence = sentence.replace("多", "");
                                    sentence = sentence.replace("，", "");
                                    for (Term term : ToAnalysis.parse(sentence)) {
                                        if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                            // if (!vo.getMoney().contains(term.getRealName())) {
                                            //     vo.getMoney().add(term.getRealName());
                                            //     vo.getMoneyString().add(sentence);
                                            // }

                                        }
                                    }
                                }

                                // if (vo.getMoney().size() == 0) {
                                //     if ((sentence.contains("非法获利")
                                //             || sentence.contains("抽头获利")
                                //             || sentence.contains("抽水渔利")
                                //             || sentence.contains("抽取头薪")
                                //             || sentence.contains("违法所得")
                                //             || sentence.contains("抽取费用")
                                //             || sentence.contains("入账为")
                                //             || sentence.contains("赌场共抽头")
                                //             || sentence.contains("牟利约")
                                //             || sentence.contains("服务费")
                                //             || sentence.contains("投注共计")
                                //             || sentence.contains("累计抽水")
                                //             || sentence.contains("赌博活动从中获利")
                                //             || sentence.contains("抢红包渔利")
                                //             || sentence.contains("发出微信红包约")
                                //             || sentence.contains("三人获利")
                                //             || sentence.contains("累计金额")
                                //             || sentence.contains("赃款")
                                //             || sentence.contains("退水费获利")
                                //             || sentence.contains("赚取")
                                //             || sentence.contains("赌资")) && sentence.contains("元")) {
                                //         sentence = sentence.replace("余", "");
                                //         sentence = sentence.replace("多", "");
                                //         sentence = sentence.replace("，", "");
                                //         for (Term term : ToAnalysis.parse(sentence)) {
                                //             if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                //                 if (!vo.getMoney().contains(term.getRealName())) {
                                //                     vo.getMoney().add(term.getRealName());
                                //                     vo.getMoneyString().add(sentence);
                                //                 }
                                //
                                //             }
                                //         }
                                //     }
                                // }
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
                    party.setAgeContent(str);
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
                    if (s.contains("住") && (!s.equals("住所地") || !s.equals("住所"))) {
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
                        if (name.contains("省") || name.contains("自治区") || name.contains("兵团") || name.contains("市") || name.contains("盟") || name.contains("自治州") || name.contains("住所地") || name.contains("住") || name.contains("户籍地")) {
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


    public void address(DocumentXsLhEntity entity, CaseVo vo) {
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

    private Set<String> illness = new HashSet<>();

    {
        illness.add("重度抑郁");
        illness.add("复发性抑郁障碍");
        illness.add("精神病症状的抑郁症");
        illness.add("抑郁症");
        illness.add("酒精所致精神障碍");
        illness.add("酒精中毒所致精神障碍");
        illness.add("酒精中毒性精神障碍");
        illness.add("特定的精神障碍");
        illness.add("精神活性物质所致的精神和行为障碍");
        illness.add("精神分裂症");
        illness.add("癫痫");
        illness.add("癔症");
        illness.add("病理性醉酒");
        illness.add("甲状腺功能亢进所致精神障碍");
        illness.add("双向情感障碍");
        illness.add("中度精神发育迟滞");
        illness.add("重度精神发育迟滞");
        illness.add("持久的妄想性障碍");
        illness.add("精神障碍");
        illness.add("边缘智力伴精神障碍");
        illness.add("情感性精神障碍");
        illness.add("急性而短暂的精神病性障碍");
        illness.add("急性而短暂的精神病性症");
        illness.add("急性而短暂的××性障碍");
        illness.add("急性短暂性精神障碍");
        illness.add("急性短暂性精神病");
        illness.add("急性应激性精神病");
        illness.add("心境障碍");
        illness.add("待分类的精神病性障碍");
        illness.add("待分类的××性障碍");
        illness.add("巫术所致精神障碍");
        illness.add("妄想、被害妄想等精神病性症");
        illness.add("复发性躁狂症");
        illness.add("复发性抑郁障碍");
        illness.add("复发性抑郁症");
        illness.add("器质性精神障碍");
        illness.add("器质性幻觉症");
        illness.add("器质性妄想症");
        illness.add("双相障碍");
        illness.add("双相情感障碍");
        illness.add("双相情感精神障碍");
        illness.add("双相障碍-伴××性症");
        illness.add("双相障碍-伴精神病症");
        illness.add("双向障碍-伴精神病性症");
        illness.add("双向障碍-伴精神病性");
        illness.add("分裂样精神病");
        illness.add("列表现符合ＣＣＭＤ-3心境");
        illness.add("分裂情感性精神病");
        illness.add("其他待分类的精神障碍");
        illness.add("偏执性精神障碍");
        illness.add("偏执型精神障碍");
        illness.add("偏执型分裂症");
        illness.add("酒精所致精神病障碍");
        illness.add("酒精所致的精神及行为障碍");
        illness.add("酒精引起精神和行为障碍");
        illness.add("伴精神病性症");
        illness.add("××病");


        illness.add("脑变性病所致精神障碍");
        illness.add("颅内感染所致精神障碍");
        illness.add("急性病毒脑炎所致精神障碍");
        illness.add("克—雅病所致精神障碍");
        illness.add("克—雅病痴呆");
        illness.add("脑炎后综合征");
        illness.add("脱髓鞘脑病所致精神障碍");
        illness.add("急性播散性脑炎和急性出血性白质脑炎所致精神障碍");
        illness.add("多发性硬化所致精神障碍");
        illness.add("精神活性物质所致精神障碍");
        illness.add("酒精所致精神障碍");
        illness.add("鸦片类物质所致精神障碍");
        illness.add("大麻类物质所致精神障碍");
        illness.add("镇静催眠药或抗焦虑药所致精神障碍");
        illness.add("兴奋剂所致精神障碍");
        illness.add("烟草所致精神障碍");
        illness.add("挥发性溶剂所致精神障碍");
        illness.add("其他或待分类的精神活性物质所致精神障碍");
        illness.add("癔症");
        illness.add("癔症性精神障碍");
        illness.add("癔症性温游");
        illness.add("癔症性身份识别障碍");
        illness.add("癔症性精神病");
        illness.add("癔症性附体障碍");
        illness.add("癔症性躯体障碍");
        illness.add("器质性精神障碍");
        illness.add("急性脑血管病所致精神障碍");
        illness.add("皮层性血管病所致精神障碍");
        illness.add("皮层下血管病所致精神障碍");
        illness.add("皮层和皮层下血管病所致精神障碍");
        illness.add("其他或待分类血管病所致精神障碍");
        illness.add("待分类血管性痴呆");
        illness.add("其他脑部疾病所致精神障碍");
        illness.add("脑变性病所致精神障碍");
        illness.add("匹克病所致精神障碍");
        illness.add("享廷顿病所致精神障碍");
        illness.add("享廷顿病痴呆");
        illness.add("匹克病痴呆");
        illness.add("巴金森病所致精神障碍");
        illness.add("巴金森病痴呆");
        illness.add("肝豆状核变性所致精神障碍");
        illness.add("颅内感染所致精神障碍");
        illness.add("急性病毒性脑炎所致精神障碍");
        illness.add("克—雅病所致精神障碍");
        illness.add("脑炎后综合征");
        illness.add("脱髓鞘脑病所致精神障碍");
        illness.add("急性播散性脑脊髓炎和急性出血性白质脑炎所致精神障碍");
        illness.add("多发性硬化所致精神障碍");
        illness.add("脑外伤所致精神障碍");
        illness.add("脑震荡后综合征");
        illness.add("脑挫裂伤后综合征");
        illness.add("脑瘤所致精神障碍");
        illness.add("癫痫所致精神障碍");
        illness.add("躯体疾病所致精神障碍");
        illness.add("躯体感染所致精神障碍");
        illness.add("人类免疫缺陷病毒所致精神障碍");
        illness.add("内脏器官疾病所致精神障碍");
        illness.add("内分泌疾病所致精神障碍");
        illness.add("营养代谢疾病所致精神障碍");
        illness.add("结缔组织疾病所致精神障碍");
        illness.add("系统性红斑狼疮所致精神障碍");
        illness.add("染色体异常所致精神障碍");
        illness.add("物理因素所致精神障碍");
        illness.add("围生期精神障碍");
        illness.add("其他或待分类器质性精神");
        illness.add("器质性智能损害");
        illness.add("器质性意识障碍");
        illness.add("器质性情感障碍");
        illness.add("器质性癔症样综合征");
        illness.add("器质性神经症样综合征");
        illness.add("器质性情绪不稳（脆弱）障碍");
        illness.add("精神活性物质所致精神障碍");
        illness.add("酒精所致精神障碍");
        illness.add("阿片类物质所致精神障碍");
        illness.add("大麻类物质所致精神障碍");
        illness.add("镇静催眠药或抗焦虑药所致精神障碍");
        illness.add("兴奋剂所致精神障碍");
        illness.add("致幻剂所致精神障碍");
        illness.add("烟草所致精神障碍");
        illness.add("挥发性溶剂所致精神障碍");
        illness.add("其他或待分类的精神活性物质所致精神障碍");
        illness.add("非成瘾物质所致精神障碍");
        illness.add("非成瘾药物所致精神障碍");
        illness.add("一氧化碳所致精神障碍");
        illness.add("有机化合物所致精神障碍");
        illness.add("重金属所致精神障碍");
        illness.add("食物所致精神障碍");
        illness.add("其他或待分类的非成瘾物质所致精神障碍");
        illness.add("精神分裂症");
        illness.add("分裂症");
        illness.add("偏执型分裂症");
        illness.add("瓦解型分裂症");
        illness.add("青春型分裂症");
        illness.add("紧张型分裂症");
        illness.add("单纯型分裂症");
        illness.add("未定型分裂症");
        illness.add("其他型或待分类的精神分裂症");
        illness.add("精神分裂症后抑郁");
        illness.add("精神分裂症缓解期");
        illness.add("精神分裂症残留期");
        illness.add("慢性精神分裂症");
        illness.add("精神分裂症衰退期");
        illness.add("偏执性精神障碍");
        illness.add("急性短暂性精神病");
        illness.add("分裂样精神病");
        illness.add("旅途性精神病");
        illness.add("妄想阵发（急性妄想发作");
        illness.add("其他或待分类的急性短暂精神病");
        illness.add("感应性精神病");
        illness.add("分裂情感性精神病");
        illness.add("分裂情感性精神病");
        illness.add("躁狂型");
        illness.add("分裂情感性精神病");
        illness.add("抑郁型");
        illness.add("分裂情感性精神病");
        illness.add("其他或待分类的精神病性障碍");
        illness.add("周期性精神病");
        illness.add("轻性躁狂症");
        illness.add("无精神病性症状的躁狂症");
        illness.add("有精神病性症状的躁狂症");
        illness.add("复发性躁狂");
        illness.add("复发性躁狂症");
        illness.add("复发性躁狂症");
        illness.add("其他或待分类的躁狂");
        illness.add("轻性抑郁症");
        illness.add("无精神病性症状的抑郁症");
        illness.add("有精神病性症状的抑郁症");
        illness.add("复发性抑郁症");
        illness.add("有精神病性症状的抑郁");
        illness.add("持续性心境障碍");
        illness.add("恶劣心境");
        illness.add("其他或待分类的持续性心境障碍");
        illness.add("其他或待分类的心境障碍");
        illness.add("意识障碍");
        illness.add("癔症性精神障碍");
        illness.add("癔症性遗忘");
        illness.add("癔症性漫游");
        illness.add("癔症性身份识别障碍");
        illness.add("癔症性精神病");
        illness.add("癔症性附体障碍");
        illness.add("癔症性木僵");
        illness.add("癔症性躯体障碍");
        illness.add("癔症性运动障碍");
        illness.add("癔症性抽搐发作");
        illness.add("癔症性感觉障碍");
        illness.add("混合性癔症躯体—精神障碍");
        illness.add("其他或待分类癔症");
        illness.add("Ganser综合征");
        illness.add("短暂的癔症性障碍");
        illness.add("应激相关障碍");
        illness.add("急性应激障碍");
        illness.add("急性应激性精神病");
        illness.add("创伤后应激障碍");
        illness.add("适应障碍");
        illness.add("偏执性人格障碍");
        illness.add("分裂样人格障碍");
        illness.add("反社会性人格障碍");
        illness.add("冲动性人格障碍");
        illness.add("攻击性人格障碍");
        illness.add("表演性人格障碍");
        illness.add("癔症性人格障碍");
        illness.add("强迫性人格障碍");
        illness.add("焦虑性人格障碍");
        illness.add("依赖性人格障碍");
        illness.add("其他或待分类的人格障碍");
        illness.add("习惯与冲动控制障碍");
        illness.add("病理性赌博");
        illness.add("病理性纵火");
        illness.add("病理性偷窃");
        illness.add("性心理障碍");
        illness.add("性身份障碍");
        illness.add("易性症");
        illness.add("其他或待分类的性身份障碍");
        illness.add("性偏好障碍");
        illness.add("恋物症");
        illness.add("异装症");
        illness.add("露阴症");
        illness.add("窥阴症");
        illness.add("磨擦症");
        illness.add("性施虐与性受虐症");
        illness.add("混合型性偏好障碍");
        illness.add("其他或待分类的性偏好障碍");
        illness.add("性指向障碍");
        illness.add("同性恋");
        illness.add("双性恋");
        illness.add("其他或待分类的性指向障碍");
        illness.add("精神发育迟滞");
        illness.add("轻度精神发育迟滞");
        illness.add("中度精神发育迟滞");
        illness.add("重度精神发育迟滞");
        illness.add("极重度精神发育迟滞");
        illness.add("其他或待分类的精神发育迟滞");
        illness.add("无或轻微的行为障碍");
        illness.add("其他或待分类的行为障碍");
        illness.add("言语和语言发育障碍");
        illness.add("特定言语构音障碍");
        illness.add("表达性语言障碍");
        illness.add("感受性语言障碍");
        illness.add("伴发癫痫的获得性失语");
        illness.add("其他或待分类的言语和语言发育障碍");
        illness.add("特定学校技能发育障碍");
        illness.add("特定阅读障碍");
        illness.add("特定拼写障碍");
        illness.add("特定计算技能障碍");
        illness.add("合性学习技能障碍");
        illness.add("其他或待分类的特定学习技能发育障碍");
        illness.add("儿童分离性焦虑症");
        illness.add("儿童恐惧症");
        illness.add("儿童社交恐惧症");
        illness.add("儿童社会功能障碍");
        illness.add("神经性厌食");
        illness.add("失眠症");
        illness.add("嗜睡症");
        illness.add("睡眠—觉醒节律障碍");
        illness.add("恐惧症");
        illness.add("场所恐惧症");
        illness.add("社交恐惧症");
        illness.add("特定的恐惧症");
        illness.add("焦虑症");
        illness.add("惊恐障碍");
        illness.add("广泛性焦虑");
        illness.add("强迫症");

    }


}
