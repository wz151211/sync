package com.ping.syncparse.service.security;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.common.DwbmCode;
import com.ping.syncparse.entity.PartyEntity;
import lombok.extern.slf4j.Slf4j;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.*;

/**
 * @Author: W.Z
 * @Date: 2023/10/13 13:24
 */
@Service
@Slf4j
public class SecurityService {
    @Autowired
    private SecurityResultMapper resultMapper;

    @Autowired
    private SecurityMapper securityMapper;

    private AtomicInteger pageNum = new AtomicInteger(0);

    private int pageSize = 20000;

    private String[] temp = {"汉族", "满族", "回族", "藏族", "苗族", "彝族", "壮族", "侗族", "瑶族", "白族", "傣族", "黎族", "佤族", "畲族", "水族", "土族", "蒙古族", "布依族", "土家族", "哈尼族", "傈僳族", "高山族", "拉祜族", "东乡族", "纳西族", "景颇族", "哈萨克族", "维吾尔族", "达斡尔族", "柯尔克孜族", "羌族", "怒族", "京族", "德昂族", "保安族", "裕固族", "仫佬族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "朝鲜族", "赫哲族", "门巴族", "珞巴族", "独龙族", "基诺族", "塔吉克族", "俄罗斯族", "鄂温克族", "塔塔尔族", "鄂伦春族", "乌孜别克族"};

    private Set<String> eduLevel = new HashSet<>();

    private static final Pattern AMOUNT_PATTERN = Pattern.compile("^(0|[1-9]\\d{0,11})\\.(\\d\\d)$"); // 不考虑分隔符的正确性

    private Set<String> nations = new HashSet<>();

    private Set<String> professionSet = new HashSet<>();

    private List<Dict> areaNewList = new ArrayList<>();

    private Set<String> tempCode = new HashSet();

    private List<Dict> causeList = new ArrayList<>();
    private List<Dict> areaList = new ArrayList<>();
    Map<String, Dict> areaMap = new HashMap<>();
    Map<String, Dict> areaCodeMap = new HashMap<>();
    private Set<String> causeSet = new HashSet<>();


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

    public void parse() {

        List<SecurityVo> entities = securityMapper.findList(2, pageSize, null);
        if (entities == null || entities.size() == 0) {
            return;
        }
        pageNum.getAndIncrement();
        entities.parallelStream().forEach(entity -> {
            SecurityResultVo vo = new SecurityResultVo();
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
                if (vo.getCity().contains("第一") || vo.getCity().contains("第二") || vo.getCity().contains("第三") || vo.getCity().contains("第四") || vo.getCity().contains("第五")) {
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
                String litigationRecords = entity.getLitigationRecords();
                String record = "";
                if (StringUtils.hasLength(litigationRecords)) {
                    litigationRecords = litigationRecords.replace("，", ",");
                    String[] temp = litigationRecords.split(",");
                    if (temp.length > 0) {
                        record = temp[0];
                    }
                }
                if (entity.getCaseNo().equals("（2022）浙02民初904号")) {
                    System.out.println("");
                }
                JSONArray array = entity.getParty();
                if (array != null && array.size() > 0) {
                    for (Object o : array) {
                        boolean isExist = false;
                        for (int i = 0; i < elements.size(); i++) {
                            Element element = elements.get(i);
                            String text = element.text();
                            if (text.equals("原告") || text.equals("被告")) {
                                if (i + 1 < elements.size()) {
                                    text = element.text() + ":”" + elements.get(i + 1).text();
                                }
                            }
                            if ((StringUtils.hasLength(record) && text.contains(record)) || text.contains("立案")) {
                                break;
                            }
                            if (text.contains(o.toString())) {
                                party = parseText(text, o.toString(), entity.getRefereeDate());
                                party.setCaseId(vo.getId());
                                party.setCaseNo(vo.getCaseNo());
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
                                    if (text.equals("原告") || text.equals("被告")) {
                                        if (i + 1 < elements.size()) {
                                            text = element.text() + ":”" + elements.get(i + 1).text();
                                        }
                                    }
                                    if ((StringUtils.hasLength(record) && text.contains(record)) || text.contains("立案")) {
                                        break;
                                    }
                                    if (StringUtils.hasLength(name) && text.contains(name)) {
                                        party = parseText(text, name, entity.getRefereeDate());
                                        party.setCaseId(vo.getId());
                                        party.setCaseNo(vo.getCaseNo());
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
                                    if (text.equals("原告") || text.equals("被告")) {
                                        if (i + 1 < elements.size()) {
                                            text = element.text() + ":”" + elements.get(i + 1).text();
                                        }
                                    }
                                    if ((StringUtils.hasLength(record) && text.contains(record)) || text.contains("立案")) {
                                        break;
                                    }
                                    if (StringUtils.hasLength(name) && text.contains(name)) {
                                        party = parseText(text, name, entity.getRefereeDate());
                                        party.setCaseId(vo.getId());
                                        party.setCaseNo(vo.getCaseNo());
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
                                if (text.equals("原告") || text.equals("被告")) {
                                    if (i + 1 < elements.size()) {
                                        text = element.text() + ":”" + elements.get(i + 1).text();
                                    }
                                }
                                if ((StringUtils.hasLength(record) && text.contains(record)) || text.contains("立案")) {
                                    break;
                                }
                                if (StringUtils.hasLength(name) && text.contains(name)) {
                                    party = parseText(text, name, entity.getRefereeDate());
                                    party.setCaseId(vo.getId());
                                    party.setCaseNo(vo.getCaseNo());
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
                                if (text.equals("原告") || text.equals("被告")) {
                                    if (i + 1 < elements.size()) {
                                        text = element.text() + ":”" + elements.get(i + 1).text();
                                    }
                                }
                                if ((StringUtils.hasLength(record) && text.contains(record)) || text.contains("立案")) {
                                    break;
                                }
                                if (StringUtils.hasLength(name) && text.contains(name)) {
                                    party = parseText(text, name, entity.getRefereeDate());
                                    party.setCaseId(vo.getId());
                                    party.setCaseNo(vo.getCaseNo());
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
                                if (text.equals("原告") || text.equals("被告")) {
                                    if (i + 1 < elements.size()) {
                                        text = element.text() + ":”" + elements.get(i + 1).text();
                                    }
                                }
                                if ((StringUtils.hasLength(record) && text.contains(record)) || text.contains("立案")) {
                                    break;
                                }
                                if (StringUtils.hasLength(name) && text.contains(name)) {
                                    party = parseText(text, name, entity.getRefereeDate());
                                    party.setCaseId(vo.getId());
                                    party.setCaseNo(vo.getCaseNo());
                                    vo.getParty().add(party);
                                    isExist = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                for (int i = 0; i < 6; i++) {
                    if (i > elements.size() - 1) {
                        continue;
                    }
                    Element element = elements.get(i);
                    String text = element.text().trim();
                    if (StringUtils.isEmpty(text)) {
                        continue;
                    }
                    if (text.equals("原告") || text.equals("被告")) {
                        if (i + 1 < 6) {
                            text = element.text() + ":”" + elements.get(i + 1).text();
                        }
                    }
                    if (text.contains("异议") || text.contains("诉讼") || text.contains("立案")) {
                        break;
                    }
                    boolean exits = false;
                    List<PartyEntity> partyList = vo.getParty();
                    if (partyList != null && partyList.size() > 0) {
                        for (PartyEntity partyEntity : partyList) {
                            if (StringUtils.hasLength(partyEntity.getName()) && text.contains(partyEntity.getName())) {
                                exits = true;
                            }
                        }
                    }
                    if (exits) {
                        continue;
                    }
                    if (text.contains(record)) {
                        break;
                    }
                    try {
                        text = text.split("。")[0];
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (text.startsWith("原告") || text.startsWith("被告") || text.startsWith("被申请人") || text.startsWith("申请机关") || text.startsWith("原申请机关") || text.startsWith("被强制医疗人") || text.startsWith("申请人") || text.startsWith("申请复议人")) {
                        try {
                            party = parseText(text, null, entity.getRefereeDate());
                            party.setCaseId(vo.getId());
                            party.setCaseNo(vo.getCaseNo());
                            vo.getParty().add(party);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }


            if (StringUtils.hasLength(entity.getHtmlContent())) {
                Document parse = Jsoup.parse(entity.getHtmlContent());
                String text = parse.text();
                vo.setText(text);
                String[] sentences = text.split("。");
                for (String sentence : sentences) {

                    if (sentence.contains("行政处罚决定书") || sentence.contains("行政处罚") || sentence.contains("证监会处罚") || sentence.contains("调查通知书")) {
                        vo.getPenalty().add(sentence);
                    }

                    String dayContent = sentence.replace(",", "，");
                    dayContent = dayContent.replace(";", "，");
                    dayContent = dayContent.replace("；", "，");

                    String[] commas = dayContent.split("，");

                    for (String comma : commas) {
                        String disclosureDay = "";

                        if (comma.contains("揭露日") && comma.contains("年") && comma.contains("月") && !comma.contains("附件") && !comma.contains("审判长")) {

                            for (Term term : ToAnalysis.parse(comma)) {
                                if (disclosureDay.contains("年") && term.getRealName().contains("年")) {
                                    disclosureDay = "";
                                }
                                if (disclosureDay.contains("月") && term.getRealName().contains("月")) {
                                    break;
                                }
                                if (disclosureDay.contains("日") && term.getRealName().contains("日")) {
                                    break;
                                }

                                String realName = term.getRealName();
                                if (realName.contains("年") && realName.length() < 5) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    disclosureDay += realName;
                                }
                            }
                            if (StringUtils.hasLength(disclosureDay)) {
                                vo.setDisclosureDay(disclosureDay);
                                vo.setDisclosureDayContent(sentence);
                            }

                        }

                        if ((comma.contains("委托") || comma.contains("根据"))
                                && (comma.contains("公司") || comma.contains("究院") || comma.contains("服务中心"))
                                && comma.contains("损失")
                                && !comma.contains("规定")
                                && StringUtils.isEmpty(vo.getApprovedBy())) {
                            int start = comma.indexOf("委托");
                            if (start == -1) {
                                start = comma.indexOf("根据");
                            }
                            int end = comma.indexOf("公司");
                            if (end == -1) {
                                end = comma.indexOf("究院");
                            }
                            if (end == -1) {
                                end = comma.indexOf("服务中心");
                                end += 2;
                            }
                            String name = "";
                            if (start < end && start != -1 && end != -1) {
                                name = comma.substring(start + 2, end + 2);
                            }
                            if (StringUtils.hasLength(name)) {
                                vo.setApprovedBy(name);
                                vo.setApprovedByContent(comma);
                            }
                        }


                        if (comma.contains("出具") && comma.contains("意见书") && comma.contains("损失")
                                && !comma.contains("第")
                                && !comma.contains("条")
                                && !comma.contains("未主张")
                                && !comma.contains("规定")
                                && StringUtils.isEmpty(vo.getApprovedDoc())) {
                            int start = sentence.indexOf("出具");
                            int end = sentence.indexOf("意见书");
                            String name = "";
                            if (start < end && start != -1 && end != -1) {
                                if (sentence.contains("》")) {
                                    name = sentence.substring(start + 2, end + 4);
                                } else {
                                    name = sentence.substring(start + 2, end + 3);
                                }
                            }
                            name = name.replace("的", "");
                            name = name.replace("了", "");
                            if (StringUtils.hasLength(name)) {
                                vo.setApprovedDoc(name);
                                vo.setApprovedDocContent(comma);
                            }
                        }
                    }
                    if (sentence.contains("移动加权平均法")
                            || sentence.contains("先入先出平均法")
                            || sentence.contains("算术平均法")) {

                        if (sentence.contains("移动加权平均法")) {
                            vo.setAveragePrice("移动加权平均法");
                        }
                        if (sentence.contains("先入先出平均法")) {
                            vo.setAveragePrice("先入先出平均法");
                        }
                        if (sentence.contains("算术平均法")) {
                            vo.setAveragePrice("算术平均法");
                        }


                        vo.setAveragePriceContent(sentence);
                    }
                    if (sentence.contains("买入均价") && (sentence.contains("采用") || sentence.contains("根据")) && sentence.contains("法") && !sentence.contains("上述") && StringUtils.isEmpty(vo.getAveragePrice())) {
                        int start = sentence.indexOf("采用");
                        if (start == -1) {
                            start = sentence.indexOf("根据");
                        }
                        int end = sentence.indexOf("法");
                        String name = "";
                        if (start < end && start != -1 && end != -1) {
                            name = sentence.substring(start, end + 1);

                        }
                        if (StringUtils.hasLength(name)) {
                            vo.setAveragePrice(name);
                            vo.setAveragePriceContent(sentence);
                        }
                    }
                    if (sentence.contains("投资者投资差额损失") || (sentence.contains("损失计算") && sentence.contains("计算公式"))) {
                        vo.setDifferenceLoss(sentence);
                    }

                    if (sentence.contains("系统性风险") || sentence.contains("系统风险")) {
                        vo.getRisk().add(sentence);
                    }


                    for (PartyEntity partyEntity : vo.getParty()) {
                        try {
                            if (partyEntity != null && StringUtils.hasLength(partyEntity.getName()) && sentence.contains(partyEntity.getName()) && "原告".equals(partyEntity.getType()) && sentence.contains("损失")) {
                                String temp = sentence.replace("，", "");
                                temp = temp.replace(",", "");
                                for (Term term : ToAnalysis.parse(temp)) {
                                    if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                        if (StringUtils.isEmpty(partyEntity.getPetitionAmount())) {
                                            Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                            if (matcher.find()) {
                                                partyEntity.setPetitionAmount(term.getRealName());
                                            } else {
                                                try {
                                                    partyEntity.setPetitionAmount(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                } catch (Exception e) {
                                                    partyEntity.setPetitionAmount(term.getRealName());
                                                }
                                            }
                                            partyEntity.setPetitionAmountContent(sentence);
                                        }
                                    } else if (term.getNatureStr().equals("m") && term.from() != null && (term.from().getRealName().contains("人民币"))) {
                                        if (StringUtils.isEmpty(partyEntity.getPetitionAmount())) {
                                            Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                            if (matcher.find()) {
                                                partyEntity.setPetitionAmount(term.getRealName());
                                            } else {
                                                try {
                                                    partyEntity.setPetitionAmount(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                } catch (Exception e) {
                                                    partyEntity.setPetitionAmount(term.getRealName());
                                                }
                                            }
                                            partyEntity.setPetitionAmountContent(sentence);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();

                        }
                        if (partyEntity != null && StringUtils.hasLength(partyEntity.getName()) && sentence.contains(partyEntity.getName()) && "被告".equals(partyEntity.getType()) && sentence.contains("判决")) {
                            String temp = sentence.replace(";", "，");
                            temp = temp.replace("；", "，");
                            temp = temp.replace("：", "，");
                            temp = temp.replace(":", "，");
                            String[] commass = temp.split("，");
                            for (String comma : commass) {
                                if (!comma.contains(partyEntity.getName())) {
                                    continue;
                                }
                                comma = comma.replace(",", "");
                                for (Term term : ToAnalysis.parse(comma)) {
                                    if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                        if (StringUtils.isEmpty(partyEntity.getPetitionAmount())) {
                                            Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                            if (matcher.find()) {
                                                partyEntity.setPetitionAmount(term.getRealName());
                                            } else {
                                                try {
                                                    partyEntity.setPetitionAmount(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                } catch (Exception e) {
                                                    partyEntity.setPetitionAmount(term.getRealName());
                                                }
                                            }
                                            partyEntity.setPetitionAmountContent(comma);
                                        }
                                    } else if (term.getNatureStr().equals("m") && term.from() != null && (term.from().getRealName().contains("人民币"))) {
                                        if (StringUtils.isEmpty(partyEntity.getPetitionAmount())) {
                                            Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                            if (matcher.find()) {
                                                partyEntity.setPetitionAmount(term.getRealName());
                                            } else {
                                                try {
                                                    partyEntity.setPetitionAmount(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                } catch (Exception e) {
                                                    partyEntity.setPetitionAmount(term.getRealName());
                                                }
                                            }
                                            partyEntity.setPetitionAmountContent(comma);
                                        }
                                    }
                                }
                            }

                        }
                    }

                }

                Elements elements = new Elements();
                Elements elementList = parse.body().children();
                for (Element element : elementList) {
                    int size = element.childNodeSize();
                    if (size > 2) {
                        Elements children = element.children();
                        elements.addAll(children);
                    } else {
                        elements.add(element);
                    }

                }
                for (Element element : elements) {
                    String sentence = element.text();
                    sentence = sentence.replace(" ", "");
                    sentence = sentence.replace("　", "");
                    if (sentence.contains("审判长")) {
                        int start = sentence.indexOf("审判长");
                        String judgeName = sentence.substring(start + 3);
                        if (judgeName.contains("代理")) {
                            int index = judgeName.indexOf("代理");
                            judgeName = judgeName.substring(0, index);
                        }
                        if (judgeName.contains("审判员")) {
                            int index = judgeName.indexOf("审判员");
                            judgeName = judgeName.substring(0, index);
                        }
                        judgeName = judgeName.replace(")", "");
                        judgeName = judgeName.replace("）", "");
                        judgeName = judgeName.replace("（", "");
                        judgeName = judgeName.replace("(", "");
                        vo.setJudgeName(judgeName);

                    }
                }


            }
            resultMapper.insert(vo);

        });
        System.out.println("执行完成");
    }

    private PartyEntity parseText(String text, String name, Date refereeDate) {
        text = text.replace("，", ",");
        text = text.replace("。", ",");
        text = text.replace("：", "");
        String[] split = text.split(",");
        PartyEntity party = new PartyEntity();
        party.setContent(text);
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            if (StringUtils.hasLength(name) && s.contains(name)) {
                party.setName(name);
            }
            if (!StringUtils.hasLength(party.getName())) {
                if (s.contains("（") && s.contains("）")) {
                    int start = s.indexOf("（");
                    int end = s.indexOf("）");
                    s = s.substring(0, start) + s.substring(end + 1);
                }
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
                            try {
                                int index = s.indexOf("被申请人");
                                party.setName(s.substring(index + 4));
                            } catch (Exception e) {
                                log.info("party={}", s);
                                e.printStackTrace();
                            }

                        }
                    }

                    if (s.contains("申请机关")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            try {
                                int index = s.indexOf("申请机关");
                                party.setName(s.substring(index + 4));
                            } catch (Exception e) {
                                log.info("party={}", s);
                                e.printStackTrace();
                            }

                        }
                    }

                    if (s.contains("申请人")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            try {
                                int index = s.indexOf("申请人");
                                party.setName(s.substring(index + 3));
                            } catch (Exception e) {
                                log.info("party={}", s);
                                e.printStackTrace();
                            }

                        }
                    }

          /*          if (s.contains("法定代理人")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            try {
                                int index = s.indexOf("法定代理人");
                                party.setName(s.substring(index + 5));
                            } catch (Exception e) {
                                log.info("party={}", s);
                                e.printStackTrace();
                            }
                        }
                    }*/
                    if (s.contains("申请复议人")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            try {
                                int index = s.indexOf("申请复议人");
                                party.setName(s.substring(index + 5));
                            } catch (Exception e) {
                                log.info("party={}", s);
                                e.printStackTrace();
                            }
                        }
                    }
                    if (s.contains("被强制医疗人")) {
                        if (!StringUtils.hasLength(party.getName())) {
                            try {
                                s = s.replace("暨", "");
                                int index = s.indexOf("被强制医疗人");
                                party.setName(s.substring(index + 6));
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
            if (s.contains("岁")) {
                party.setAge(s.replace("岁", ""));
            }
            if (s.contains("年龄")) {
                party.setAge(s.replace("年龄", ""));
            }
            if (s.contains("年") && s.contains("月") && s.contains("日") && s.contains("生") && StringUtils.isEmpty(party.getBirthday())) {

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
                    if (StringUtils.isEmpty(party.getAge()) && refereeDate != null) {
                        party.setAge(DateUtil.age(DateUtil.parse(str), refereeDate) + "");
                        party.setAgeContent(str);
                    }
                } catch (Exception e) {
                    log.error("s={}", s);
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
                if ((s.contains("省") || s.contains("自治区") || s.contains("兵团"))
                        && (s.contains("市") || s.contains("自治州") || s.contains("盟"))
                        && (s.contains("县") || s.contains("区") || s.contains("旗"))) {
                    if (!s.contains("检察院") && !s.contains("法院") && !s.contains("公安局") && !s.contains("公司") && !s.contains("看守所") && !s.contains("羁押")) {
                        party.setAddress(s);
                    }

                }
            }

            if (StringUtils.isEmpty(party.getAddress())) {
                if ((s.contains("省") || s.contains("自治区") || s.contains("兵团"))
                        || (s.contains("市") || s.contains("自治州") || s.contains("盟"))
                        && (s.contains("县") || s.contains("区") || s.contains("旗"))) {
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
                            break;
                        }
                    }
                }
            }
            if (text.contains("反诉被告") || text.contains("被执行人") || text.contains("被申请人") || text.contains("被上诉人") || text.contains("被申诉人") || text.contains("被强制医疗人")) {
                party.setType("被告");
            } else if (text.contains("反诉原告") || text.contains("申请执行人") || text.contains("申请人") || text.contains("自诉人") || text.contains("再审申请人") || text.contains("申诉人") || text.contains("上诉人") || text.contains("申请机关") || text.contains("申请复议人")) {
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
/*            String idCard = "";
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
                    if (temp.contains("住") && end == -1) {
                        end = temp.lastIndexOf("住");
                    }

                    Pattern pattern = Pattern.compile("^[0-9]*");
                    Matcher matcher = pattern.matcher(temp);
                    if (matcher.find()) {
                        if (end > -1) {
                            temp = temp.substring(0, end);
                        }
                    }

                }
                //   log.info("身份证号={}  ==  {}", temp, idCard);
                if (StringUtils.isEmpty(party.getIdCard()) && StringUtils.hasLength(temp) && !temp.contains("族")) {
                    party.setIdCard(temp);
                }
            }*/
   /*         if (!StringUtils.hasLength(party.getHasCriminalRecord())) {
                if (s.contains("刑满释放") || s.contains("因犯") || s.contains("曾因")) {
                    party.setHasCriminalRecord("是");
                }
            }*/
        }
        return party;
    }

    private void address(SecurityVo entity, SecurityResultVo vo) {
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
        eduLevel.add("教师");
        eduLevel.add("老师");
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
