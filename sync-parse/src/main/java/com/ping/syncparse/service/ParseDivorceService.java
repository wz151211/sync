package com.ping.syncparse.service;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.common.DwbmCode;
import com.ping.syncparse.entity.AreaEntity;
import com.ping.syncparse.entity.PartyEntity;
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
public class ParseDivorceService {
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
            CaseVo vo = new CaseVo();
            vo.setId(entity.getId());
            if (!StringUtils.hasLength(entity.getTId())) {
                vo.setTId(entity.getCaseNo());
            }
            if (StringUtils.hasLength(entity.getTId())) {
                vo.setTId(entity.getTId());
            }
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
            //   vo.setMoneySet(parseMoney(party, s25));

               /* if ("刑事案件".equals(entity.getCaseType())) {
                    String s27 = object.getString("s27");
                    //    vo.setJudgmenResultContent(s27);
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
                        }
                    }
                }*/

            if ("民事案件".equals(entity.getCaseType())) {
                String judgmentResult = entity.getJudgmentResult();
                if (StringUtils.hasLength(judgmentResult)) {
                    judgmentResult = judgmentResult.replace("；", "。");
                    for (String s : judgmentResult.split("。")) {
                        if (!s.contains("受理费") && !s.contains("诉讼费")) {
                            continue;
                        }
                        s = s.replace(",", "，");
                        s = s.replace("、", "，");
                        if (s.contains("（") && s.contains("）")) {
                            try {
                                int start = s.indexOf("（");
                                int end = s.indexOf("）");
                                if (end - start > 5) {
                                    s = s.substring(0, start) + s.substring(end + 1);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        for (String s1 : s.split("，")) {
                            if (!StringUtils.hasLength(s1)) {
                                continue;
                            }
                            if (s1.contains("一审") && s1.contains("受理费") && !"民事一审".equals(vo.getTrialProceedings())) {
                                continue;
                            }
                            if (s1.contains("受理费") || s1.contains("减半") || s1.contains("诉讼费")) {
                                if ((StringUtils.hasLength(vo.getHearingFees()) && s1.contains("减半")) || !StringUtils.hasLength(vo.getHearingFees())) {
                                    for (Term term : ToAnalysis.parse(s1)) {
                                        if ((term.getNatureStr().equals("mq") && term.getRealName().contains("元")) || ((term.from() != null && (term.from().getRealName().contains("受理费") || term.from().getRealName().contains("受理") || term.from().getRealName().contains("人民币"))) && term.getNatureStr().equals("mq"))) {
                                            Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                            if (matcher.find()) {
                                                vo.setHearingFees(term.getRealName());
                                            } else {
                                                try {
                                                    vo.setHearingFees(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                } catch (Exception e) {
                                                    vo.setHearingFees(term.getRealName());
                                                }
                                            }
                                        }
                                    }
                                }
                                if (!StringUtils.hasLength(vo.getHearingFees())) {
                                    int start = s1.indexOf("受理费");
                                    if (start == -1) {
                                        start = s1.indexOf("诉讼费");
                                    }
                                    int end = s1.indexOf("元");
                                    if (end > start + 3) {
                                        String s2 = s1.substring(start + 3, end);
                                        Matcher matcher = AMOUNT_PATTERN.matcher(s2);
                                        if (matcher.find()) {
                                            vo.setHearingFees(s2 + "元");
                                        } else {
                                            try {
                                                vo.setHearingFees(NumberChineseFormatter.chineseToNumber(s2) + "元");
                                            } catch (Exception e) {
                                                vo.setHearingFees(s2 + "元");
                                            }
                                        }
                                    }
                                }
                            }

                            if (vo.getParty() != null && vo.getParty().size() > 0) {
                                for (PartyEntity partyEntity : vo.getParty()) {
                                    if (partyEntity.getName() == null) {
                                        continue;
                                    }
                                    if ((s1.contains("负担") || s1.contains("承担")) && s1.contains("元")) {
                                        for (Term term : ToAnalysis.parse(s1)) {
                                            if ((term.getNatureStr().equals("mq") && term.getRealName().contains("元")) || ((term.from() != null && (term.from().getRealName().contains("受理费") || term.from().getRealName().contains("人民币"))) && term.getNatureStr().equals("mq"))) {
                                                if (("原告".equals(partyEntity.getType()) && s1.contains(partyEntity.getName())) || (s1.contains("原告") || s1.contains("上诉人"))) {
                                                    if (!StringUtils.hasLength(vo.getPlaintiffHearingFees())) {
                                                        Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                                        if (matcher.find()) {
                                                            vo.setPlaintiffHearingFees(term.getRealName());
                                                        } else {
                                                            try {
                                                                vo.setPlaintiffHearingFees(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                            } catch (Exception e) {
                                                                vo.setPlaintiffHearingFees(term.getRealName());
                                                            }
                                                        }
                                                    }
                                                }
                                                if (("被告".equals(partyEntity.getType()) && s1.contains(partyEntity.getName())) || (s1.contains("被告") || s1.contains("被上诉人"))) {
                                                    if (!StringUtils.hasLength(vo.getDefendantHearingFees())) {
                                                        Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                                        if (matcher.find()) {
                                                            vo.setDefendantHearingFees(term.getRealName());
                                                        } else {
                                                            try {
                                                                vo.setDefendantHearingFees(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                            } catch (Exception e) {
                                                                vo.setDefendantHearingFees(term.getRealName());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else if (s1.contains(partyEntity.getName()) && (s1.contains("负担") || s1.contains("承担"))) {
                                        if ("原告".equals(partyEntity.getType())) {
                                            if (!StringUtils.hasLength(vo.getPlaintiffHearingFees())) {
                                                vo.setPlaintiffHearingFees(vo.getHearingFees());
                                            }
                                        }
                                        if ("被告".equals(partyEntity.getType())) {
                                            if (!StringUtils.hasLength(vo.getDefendantHearingFees())) {
                                                vo.setDefendantHearingFees(vo.getHearingFees());
                                            }
                                        }
                                    } else if (s1.contains("负担") || s1.contains("承担")) {
                                        if (s1.contains("原告") || s1.equals("上诉人")) {
                                            if (!StringUtils.hasLength(vo.getPlaintiffHearingFees())) {
                                                vo.setPlaintiffHearingFees(vo.getHearingFees());
                                            }
                                        }
                                        if (s1.contains("被告") || s1.contains("被上诉人")) {
                                            if (!StringUtils.hasLength(vo.getDefendantHearingFees())) {
                                                vo.setDefendantHearingFees(vo.getHearingFees());
                                            }
                                        }
                                    }
                                }
                            } else {
                                if (s1.contains("负担") || s1.contains("承担")) {
                                    for (Term term : ToAnalysis.parse(s1)) {
                                        if ((term.getNatureStr().equals("mq") && term.getRealName().contains("元")) || ((term.from() != null && (term.from().getRealName().contains("受理费") || term.from().getRealName().contains("人民币"))) && term.getNatureStr().equals("mq"))) {
                                            if (s1.contains("原告") || s1.equals("上诉人")) {
                                                if (!StringUtils.hasLength(vo.getPlaintiffHearingFees())) {
                                                    Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                                    if (matcher.find()) {
                                                        vo.setPlaintiffHearingFees(term.getRealName());
                                                    } else {
                                                        try {
                                                            vo.setPlaintiffHearingFees(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                        } catch (Exception e) {
                                                            vo.setPlaintiffHearingFees(term.getRealName());
                                                        }
                                                    }
                                                }
                                            }
                                            if (s1.contains("被告") || s1.contains("被上诉人")) {
                                                if (!StringUtils.hasLength(vo.getDefendantHearingFees())) {
                                                    Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                                    if (matcher.find()) {
                                                        vo.setDefendantHearingFees(term.getRealName());
                                                    } else {
                                                        try {
                                                            vo.setDefendantHearingFees(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                                        } catch (Exception e) {
                                                            vo.setDefendantHearingFees(term.getRealName());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if (s1.contains("原告") || s1.equals("上诉人")) {
                                    if (!StringUtils.hasLength(vo.getPlaintiffHearingFees())) {
                                        vo.setPlaintiffHearingFees(vo.getHearingFees());
                                    }
                                }
                                if (s1.contains("被告") || s1.contains("被上诉人")) {
                                    if (!StringUtils.hasLength(vo.getDefendantHearingFees())) {
                                        vo.setDefendantHearingFees(vo.getHearingFees());
                                    }
                                }
                            }
                        }
                    }

                    judgmentResult = entity.getJudgmentResult();
                    if (judgmentResult.contains("维持原判")) {
                        if (StringUtils.hasLength(entity.getTId())) {
                            List<DocumentXsLhEntity> list = documentXsMapper.find(entity.getTId());
                            if (list != null && list.size() > 0) {
                                DocumentXsLhEntity xsLh = list.get(0);
                                judgmentResult = xsLh.getJudgmentResult();
                            }
                        }
                    }
                    if (StringUtils.hasLength(judgmentResult)) {
                        if ((judgmentResult.contains("准许") || judgmentResult.contains("本案按")) && (judgmentResult.contains("撤回起诉") || judgmentResult.contains("撤诉"))) {
                            vo.setJudgmentDesc("撤回起诉");
                        } /*else if (judgmentResult.contains("维持原判")) {
                            vo.setJudgmentDesc("维持原判");

                        }*/ else if (judgmentResult.contains("驳回原告") && judgmentResult.contains("全部诉讼请求")) {
                            // vo.setJudgmentDesc("驳回诉讼请求");
                            vo.setJudgmentDesc("被告胜诉");
                        } else {
                            if (StringUtils.hasLength(vo.getPlaintiffHearingFees()) && !StringUtils.hasLength(vo.getDefendantHearingFees())) {
                                vo.setJudgmentDesc("被告胜诉");
                            } else if (!StringUtils.hasLength(vo.getPlaintiffHearingFees()) && StringUtils.hasLength(vo.getDefendantHearingFees())) {
                                vo.setJudgmentDesc("原告胜诉");
                            } else if (StringUtils.hasLength(vo.getPlaintiffHearingFees()) && StringUtils.hasLength(vo.getDefendantHearingFees())) {

                                double a = 0;
                                double b = 0;
                                try {
                                    a = Double.parseDouble(vo.getPlaintiffHearingFees().replace("元", ""));
                                } catch (NumberFormatException e) {
                                    try {
                                        String plaintiffHearingFees = vo.getPlaintiffHearingFees();
                                        plaintiffHearingFees = plaintiffHearingFees.replace("1", "一");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("2", "二");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("3", "三");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("4", "四");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("5", "五");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("6", "六");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("7", "七");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("8", "八");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("9", "九");
                                        plaintiffHearingFees = plaintiffHearingFees.replace("元", "");
                                        a = NumberChineseFormatter.chineseToNumber(plaintiffHearingFees);
                                    } catch (Exception ex) {
                                        log.info("原告={}", vo.getPlaintiffHearingFees());
                                        ex.printStackTrace();
                                    }
                                }

                                try {
                                    b = Double.parseDouble(vo.getDefendantHearingFees().replace("元", ""));
                                } catch (NumberFormatException e) {
                                    try {
                                        String defendantHearingFees = vo.getDefendantHearingFees();
                                        defendantHearingFees = defendantHearingFees.replace("1", "一");
                                        defendantHearingFees = defendantHearingFees.replace("2", "二");
                                        defendantHearingFees = defendantHearingFees.replace("3", "三");
                                        defendantHearingFees = defendantHearingFees.replace("4", "四");
                                        defendantHearingFees = defendantHearingFees.replace("5", "五");
                                        defendantHearingFees = defendantHearingFees.replace("6", "六");
                                        defendantHearingFees = defendantHearingFees.replace("7", "七");
                                        defendantHearingFees = defendantHearingFees.replace("8", "八");
                                        defendantHearingFees = defendantHearingFees.replace("9", "九");
                                        defendantHearingFees = defendantHearingFees.replace("元", "");
                                        b = NumberChineseFormatter.chineseToNumber(defendantHearingFees);
                                    } catch (Exception ex) {
                                        log.info("被告={}", vo.getDefendantHearingFees());
                                        ex.printStackTrace();
                                    }
                                }

                                if (a > b) {
                                    vo.setJudgmentDesc("被告胜诉");
                                } else {
                                    vo.setJudgmentDesc("原告胜诉");
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
                        if (name.contains("省") || name.contains("自治区") || name.contains("兵团") || name.contains("市") || name.contains("盟") || name.contains("自治州") || name.contains("住所地")) {
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
}
