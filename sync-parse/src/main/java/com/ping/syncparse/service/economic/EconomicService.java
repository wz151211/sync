package com.ping.syncparse.service.economic;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.common.DwbmCode;
import com.ping.syncparse.entity.AreaEntity;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.service.AreaService;
import lombok.extern.slf4j.Slf4j;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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
public class EconomicService {

    @Autowired
    private EconomicTempMapper economicTempMapper;
    @Autowired
    private EconomicResultMapper economicResultMapper;
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


        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    AtomicInteger count = new AtomicInteger();

    public void parse() {
        List<EconomicTempVo> entities = economicTempMapper.findList(pageNum.get(), pageSize, null);
        if (entities == null || entities.size() == 0) {
            return;
        }
        pageNum.getAndIncrement();
        entities.parallelStream().forEach(entity -> {
            EconomicResultVo vo = new EconomicResultVo();
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
            String fact = entity.getFact();
            if ((StringUtils.isEmpty(fact) || (StringUtils.hasLength(fact) && fact.length() < 10)) && StringUtils.hasLength(entity.getHtmlContent())) {
                Document parse = Jsoup.parse(entity.getHtmlContent());
                String text = parse.text();
                int index = text.indexOf("本院认为");
                if (index > 0) {
                    text = text.substring(0, index);
                }
                fact = text;
            } else {
                fact = entity.getLitigationRecords() + fact + entity.getCourtConsidered();
            }
            String startFact = "";
            String monthsContent = "";
            if (StringUtils.hasLength(fact)) {

                for (String sentence : fact.split("。")) {
                    sentence = sentence.replace(";", "，");
                    sentence = sentence.replace("；", "，");
                    sentence = sentence.replace("：", "，");
                    String[] split = sentence.split("，");
                    for (int i = 0; i < split.length; i++) {
                        String comma = split[i];
                        comma = comma.replace("Ｏ", "0");
                        comma = comma.replace("ｌ", "0");
                        comma = comma.replace(" ", "");
                        comma = comma.replace("日前", "日");
                        String temp = "";
                        if (i > 0) {
                            temp = split[i - 1] + "，" + comma;
                        } else {
                            temp = comma;
                        }
                /*        if (((comma.contains("签订") || comma.contains("签署") || comma.contains("填写") || comma.contains("通知") || comma.contains("进入") || comma.contains("到被告") || comma.contains("从事"))
                                && (comma.contains("合同") || comma.contains("合约") || comma.contains("协议") || comma.contains("请表") || comma.contains("报道") || comma.contains("工作")))
                                && !comma.contains("未") && !comma.contains("没有")
                                || (comma.contains("合同签订时间") || comma.contains("办理入职"))) {*/
                        if (((comma.contains("签订") || comma.contains("签署") || comma.contains("填写") || comma.contains("订立"))
                                && (comma.contains("合同") || comma.contains("合约") || comma.contains("协议") || comma.contains("请表")))
                                && !comma.contains("未") && !comma.contains("没有") && !comma.contains("不签订") && !temp.contains("年满") && !temp.contains("职至")
                                || (comma.contains("合同签订时间") || comma.contains("办理入职"))) {
                            String contractSigningDate = "";
                            for (Term term : ToAnalysis.parse(temp)) {
                                if (contractSigningDate.contains("年") && term.getRealName().contains("年")) {
                                    break;
                                }
                                if (contractSigningDate.contains("月") && term.getRealName().contains("月")) {
                                    break;
                                }
                                if (contractSigningDate.contains("日") && term.getRealName().contains("日")) {
                                    break;
                                }
                                String realName = term.getRealName();
                                if (realName.contains("年") && realName.length() < 4) {
                                    continue;
                                }
                                if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    if (realName.contains(".")) {
                                        int index1 = realName.indexOf(".");
                                        try {
                                            realName = realName.substring(index1 + 1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    contractSigningDate += realName;
                                }
                            }
                            if (StringUtils.isEmpty(vo.getContractSigningDate()) && StringUtils.hasLength(contractSigningDate)) {
                                vo.setContractSigningDate(contractSigningDate);
                                vo.setContractSigningDateContent(temp);
                            }
                        }


                        if ((temp.contains("期限") || temp.contains("最后一期")) && (temp.contains("至") || (temp.contains("起") && temp.contains("到"))) && temp.contains("年") && temp.contains("月") && temp.contains("日") && !temp.contains("截止") && !temp.contains("截至") && !temp.contains("证明") && !temp.contains("延长")) {
                            String[] data = {};
                            if (temp.contains("到")) {
                                data = temp.split("到");
                            } else if (temp.contains("至")) {
                                data = temp.split("至");
                            }
                            if (data.length == 2 && data[0].contains("年") && data[1].contains("年")) {
                                String contractStartDate = "";
                                try {
                                    for (Term term : ToAnalysis.parse(data[0])) {
                                        if (contractStartDate.contains("年") && term.getRealName().contains("年")) {
                                            break;
                                        }
                                        if (contractStartDate.contains("月") && term.getRealName().contains("月")) {
                                            break;
                                        }
                                        if (contractStartDate.contains("日") && term.getRealName().contains("日")) {
                                            break;
                                        }
                                        String realName = term.getRealName();
                                        if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                            continue;
                                        }
                                        if (realName.contains("年") && realName.length() < 5) {
                                            continue;
                                        }
                                        if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                            if (realName.contains(".")) {
                                                int index1 = realName.indexOf(".");
                                                try {
                                                    realName = realName.substring(index1 + 1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            contractStartDate += realName;
                                        }
                                    }
                                } catch (Exception e) {
                                    log.info("起止日期={}", sentence);
                                    e.printStackTrace();
                                }
                                String contractEndDate = "";
                                try {
                                    for (Term term : ToAnalysis.parse(data[1])) {
                                        if (contractEndDate.contains("年") && term.getRealName().contains("年")) {
                                            break;
                                        }
                                        if (contractEndDate.contains("月") && term.getRealName().contains("月")) {
                                            break;
                                        }
                                        if (contractEndDate.contains("日") && term.getRealName().contains("日")) {
                                            break;
                                        }
                                        String realName = term.getRealName();
                                        if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                            continue;
                                        }
                                        if (realName.contains("年") && realName.length() < 5) {
                                            continue;
                                        }
                                        if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                            if (realName.contains(".")) {
                                                int index1 = realName.indexOf(".");
                                                try {
                                                    realName = realName.substring(index1 + 1);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                            contractEndDate += realName;
                                        }
                                    }
                                } catch (Exception e) {
                                    log.info("起止日期={}", sentence);
                                    e.printStackTrace();
                                }

                                if (StringUtils.isEmpty(vo.getContractStartDate()) && StringUtils.hasLength(contractStartDate)) {
                                    vo.setContractStartDate(contractStartDate);
                                    vo.setContractStartDateContent(temp);
                                }
                                if (StringUtils.isEmpty(vo.getContractEndDate()) && StringUtils.hasLength(contractEndDate)) {
                                    vo.setContractEndDate(contractEndDate);
                                    vo.setContractEndDateContent(temp);
                                }
                            }
                        }
                        if ((comma.contains("期限"))
                                && (comma.contains("个月") || (comma.contains("年") && !comma.contains("月")) || (comma.contains("借期") && comma.contains("期")))
                                && !comma.contains("保证")
                                && !comma.contains("届满")
                                && !comma.contains("不超过")
                                && !comma.contains("以下的")
                                && !comma.contains("以内")
                                && !comma.contains("以上")
                                && !comma.contains("不满")
                                && StringUtils.isEmpty(monthsContent)) {
                            monthsContent = comma;
                        }
                        Pattern stages = Pattern.compile("分[0-9]+期");
                        if (stages.matcher(comma).find() && StringUtils.isEmpty(monthsContent)) {
                            monthsContent = comma;
                        }

                        if ((temp.contains("期限") || temp.contains("起始时间")) && temp.contains("年") && temp.contains("月") && temp.contains("日") && !temp.contains("期限至") && !temp.contains("延长")) {
                            String contractStartDate = "";
                            for (Term term : ToAnalysis.parse(temp)) {
                                if (contractStartDate.contains("年") && term.getRealName().contains("年")) {
                                    break;
                                }
                                if (contractStartDate.contains("月") && term.getRealName().contains("月")) {
                                    break;
                                }
                                if (contractStartDate.contains("日") && term.getRealName().contains("日")) {
                                    break;
                                }
                                String realName = term.getRealName();
                                if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                    continue;
                                }
                                if (realName.contains("年") && realName.length() < 5) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    if (realName.contains(".")) {
                                        int index1 = realName.indexOf(".");
                                        try {
                                            realName = realName.substring(index1 + 1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    contractStartDate += realName;
                                }
                            }
                            if (StringUtils.isEmpty(vo.getContractStartDate()) && StringUtils.hasLength(contractStartDate)) {
                                vo.setContractStartDate(contractStartDate);
                                vo.setContractStartDateContent(temp);
                            }
                        }

                        if ((temp.contains("平均工资") || temp.contains("每月基本工资") || (temp.contains("平均") && temp.contains("工资")) || temp.contains("每月应发工资") || temp.contains("同意按月工资") || temp.contains("工资标准") || temp.contains("基本工资")) && (!temp.contains("至") || temp.contains("计算依据")) && !temp.contains("部分工资")) {
                            for (Term term : ToAnalysis.parse(comma)) {
                                if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                    if (StringUtils.isEmpty(vo.getAverageWage())) {
                                        Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                        if (matcher.find()) {
                                            vo.setAverageWage(term.getRealName());
                                        } else {
                                            try {
                                                vo.setAverageWage(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                            } catch (Exception e) {
                                                vo.setAverageWage(term.getRealName());
                                            }
                                        }
                                        vo.setAverageWageContent(temp);
                                    }
                                } else if (term.getNatureStr().equals("m") && term.from() != null && (term.from().getRealName().contains("人民币"))) {
                                    if (StringUtils.isEmpty(vo.getAverageWage())) {
                                        Matcher matcher = AMOUNT_PATTERN.matcher(term.getRealName().replace("元", ""));
                                        if (matcher.find()) {
                                            vo.setAverageWage(term.getRealName());
                                        } else {
                                            try {
                                                vo.setAverageWage(NumberChineseFormatter.chineseToNumber(term.getRealName().replace("元", "")) + "元");
                                            } catch (Exception e) {
                                                vo.setAverageWage(term.getRealName());
                                            }
                                        }
                                        vo.setAverageWageContent(temp);
                                    }
                                }
                            }
                        }

                        if ((temp.contains("到期") || temp.contains("期满") || temp.contains("期限至") || temp.contains("续签至") || temp.contains("延期") || temp.contains("延长")) && temp.contains("年") && temp.contains("月") && temp.contains("日") && !temp.contains("到期后")) {
                            String contractEndDate = "";
                            int index = temp.indexOf("延长");
                            if (index != -1) {
                                temp = temp.substring(index);
                            }
                            for (Term term : ToAnalysis.parse(temp)) {
                                if (contractEndDate.contains("年") && term.getRealName().contains("年")) {
                                    break;
                                }
                                if (contractEndDate.contains("月") && term.getRealName().contains("月")) {
                                    break;
                                }
                                if (contractEndDate.contains("日") && term.getRealName().contains("日")) {
                                    break;
                                }
                                String realName = term.getRealName();
                                if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                    continue;
                                }
                                if (realName.contains("年") && realName.length() < 5) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    if (realName.contains(".")) {
                                        int index1 = realName.indexOf(".");
                                        try {
                                            realName = realName.substring(index1 + 1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    contractEndDate += realName;
                                }
                            }
                            if ((StringUtils.isEmpty(vo.getContractEndDate()) && StringUtils.hasLength(contractEndDate)) || (temp.contains("续签至") || temp.contains("延期") || temp.contains("延长"))) {
                                vo.setContractEndDate(contractEndDate);
                                vo.setContractEndDateContent(temp);
                            }
                        }

                        if ((temp.contains("解除") || temp.contains("实际工作至") || temp.contains("辞退通知书") || temp.contains("不在被告处工作") || temp.contains("处理决定") || temp.contains("协商一致") || temp.contains("工作至")) && temp.contains("年") && temp.contains("月") && !temp.contains("没有") && !temp.contains("未") && !temp.contains("未提供") && !temp.contains("经济补偿金") && !temp.contains("赔偿金") && !temp.contains("签订") && !temp.contains("判令") && !temp.contains("罚单") && !temp.contains("存在劳动关系")
                                && StringUtils.isEmpty(vo.getDefaultDate())) {
                            String defaultDate = "";
                            String t = temp.replace("月经", "月");
                            for (Term term : ToAnalysis.parse(t)) {
                                if (defaultDate.contains("年") && term.getRealName().contains("年")) {
                                    break;
                                }
                                if (defaultDate.contains("月") && term.getRealName().contains("月")) {
                                    break;
                                }
                                if (defaultDate.contains("日") && term.getRealName().contains("日")) {
                                    break;
                                }
                                String realName = term.getRealName();
                                if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    if (realName.contains(".")) {
                                        int index1 = realName.indexOf(".");
                                        try {
                                            realName = realName.substring(index1 + 1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    defaultDate += realName;
                                }
                            }
                            if (StringUtils.isEmpty(vo.getDefaultDate())) {
                                vo.setDefaultDate(defaultDate);
                                vo.setDefaultDateContent(temp);
                            }
                        }

                        if (((temp.contains("通知") || temp.contains("进入") || temp.contains("到被告") || temp.contains("从事") || temp.contains("办理") || temp.contains("在") || temp.contains("建立") || temp.contains("招聘") || temp.contains("入职") || temp.contains("到"))
                                && (temp.contains("报道") || temp.contains("工作") || temp.contains("入职") || temp.contains("上班") || temp.contains("合同关系")))
                                && !temp.contains("未") && !temp.contains("没有") && !temp.contains("不在") && !temp.contains("工作时间") && !temp.contains("成立")
                                && temp.contains("年")) {
                            String actualDate = "";
                            String t = temp.replace("月入", "月");
                            for (Term term : ToAnalysis.parse(t)) {
                                if (actualDate.contains("年") && term.getRealName().contains("年")) {
                                    break;
                                }
                                if (actualDate.contains("月") && term.getRealName().contains("月")) {
                                    break;
                                }
                                if (actualDate.contains("日") && term.getRealName().contains("日")) {
                                    break;
                                }
                                String realName = term.getRealName();
                                if (realName.contains("年") && realName.length() < 4) {
                                    continue;
                                }
                                if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    if (realName.contains(".")) {
                                        int index1 = realName.indexOf(".");
                                        try {
                                            realName = realName.substring(index1 + 1);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    actualDate += realName;
                                }
                            }
                            if (StringUtils.isEmpty(vo.getActualDate()) && StringUtils.hasLength(actualDate)) {
                                vo.setActualDate(actualDate);
                                vo.setActualDateContent(temp);
                            }
                        }
                    }
                }
            }
            if (StringUtils.hasLength(startFact)) {
                startFact = startFact + entity.getJudgmentResult();
                startFact.replace(";", "，");
                startFact.replace("；", "，");
                startFact.replace("。", "，");
                String[] split = startFact.split("，");
                for (int i = 0; i < split.length; i++) {
                    String comma = split[i];
                    if (i > 0) {
                        comma = split[i - 1] + comma;
                    }
                    if ((comma.contains("期限") || comma.contains("起始时间") || comma.contains("借期")) && (comma.contains("至") || (comma.contains("起") && comma.contains("到"))) && comma.contains("年") && comma.contains("月") && comma.contains("日") && !comma.contains("截止")) {
                        String[] data = {};
                        if (comma.contains("到")) {
                            data = comma.split("到");
                        } else if (comma.contains("至")) {
                            data = comma.split("至");
                        }
                        if (data.length == 2 && data[0].contains("年") && data[1].contains("年")) {
                            String contractStartDate = "";
                            try {
                                for (Term term : ToAnalysis.parse(data[0])) {
                                    if (contractStartDate.contains("年") && term.getRealName().contains("年")) {
                                        break;
                                    }
                                    if (contractStartDate.contains("月") && term.getRealName().contains("月")) {
                                        break;
                                    }
                                    if (contractStartDate.contains("日") && term.getRealName().contains("日")) {
                                        break;
                                    }
                                    String realName = term.getRealName();
                                    if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                        continue;
                                    }
                                    if (realName.contains("年") && realName.length() < 5) {
                                        continue;
                                    }
                                    if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                        if (realName.contains(".")) {
                                            int index1 = realName.indexOf(".");
                                            try {
                                                realName = realName.substring(index1 + 1);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        contractStartDate += realName;
                                    }
                                }
                            } catch (Exception e) {
                                log.info("起止日期={}", comma);
                                e.printStackTrace();
                            }
                            String contractEndDate = "";
                            try {
                                for (Term term : ToAnalysis.parse(data[1])) {
                                    if (contractEndDate.contains("年") && term.getRealName().contains("年")) {
                                        break;
                                    }
                                    if (contractEndDate.contains("月") && term.getRealName().contains("月")) {
                                        break;
                                    }
                                    if (contractEndDate.contains("日") && term.getRealName().contains("日")) {
                                        break;
                                    }
                                    String realName = term.getRealName();
                                    if (realName.contains("个") || realName.contains("翌日") || realName.contains("当日") || realName.contains("次日") || realName.contains("期日") || realName.contains("次月")) {
                                        continue;
                                    }
                                    if (realName.contains("年") && realName.length() < 5) {
                                        continue;
                                    }
                                    if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                        if (realName.contains(".")) {
                                            int index1 = realName.indexOf(".");
                                            try {
                                                realName = realName.substring(index1 + 1);
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        contractEndDate += realName;
                                    }
                                }
                            } catch (Exception e) {
                                log.info("起止日期={}", comma);
                                e.printStackTrace();
                            }

                            if (StringUtils.isEmpty(vo.getContractStartDate()) && StringUtils.hasLength(contractStartDate)) {
                                vo.setContractStartDate(contractStartDate);
                                vo.setContractStartDateContent(comma);
                            }
                            if (StringUtils.isEmpty(vo.getContractEndDate()) && StringUtils.hasLength(contractEndDate)) {
                                vo.setContractEndDate(contractEndDate);
                                vo.setContractEndDateContent(comma);
                            }
                        }
                    }
                }
            }


            if (StringUtils.hasLength(monthsContent) && StringUtils.hasLength(vo.getContractStartDate())) {
                vo.setTermContent(monthsContent);
          /*      if (StringUtils.isEmpty(vo.getContractStartDate()) && StringUtils.hasLength(vo.getContractSigningDate())) {
                    vo.setContractStartDate(vo.getContractSigningDate());
                    vo.setContractStartDateContent(vo.getContractSigningDateContent());
                }*/
                Integer month = 0;
                // log.info("月份={}", monthsContent);
                for (Term term : ToAnalysis.parse(monthsContent)) {
                    if ((term.getNatureStr().equals("t") && (term.getRealName().contains("个") || (term.getRealName().contains("年") && term.getRealName().length() <= 3)))
                            || (term.getNatureStr().equals("mq") && term.getRealName().contains("期"))) {
                        String realName = term.getRealName();
                        realName = realName.replace("个", "");
                        realName = realName.replace("月", "");
                        realName = realName.replace("年", "");
                        realName = realName.replace("期", "");
                        try {
                            month = Integer.parseInt(realName);
                        } catch (NumberFormatException e) {
                            try {
                                month = NumberChineseFormatter.chineseToNumber(realName);
                            } catch (Exception ex) {
                                log.error("月份2={}", monthsContent);
                                ex.printStackTrace();
                            }
                            log.error("月份1={}", monthsContent);
                        }
                        if (term.getRealName().contains("年")) {
                            month = month * 12;
                        }
                        vo.setTerm(month);

                    }
                }
                if (StringUtils.hasLength(vo.getContractStartDate()) && StringUtils.isEmpty(vo.getContractEndDate())) {
                    String startDate = vo.getContractStartDate();
                    try {
                        if (startDate.contains("年") && startDate.contains("月") && !startDate.contains("日")) {
                            int index = startDate.indexOf("月");
                            if (index > -1) {
                                startDate = startDate.substring(0, index + 1);
                                startDate = startDate + "01日";
                            }
                        }
                        DateTime dateTime = DateUtil.parse(startDate);
                        DateTime endDate = DateUtil.offsetMonth(dateTime, month);
                        String date = DateUtil.formatChineseDate(endDate, false, false);
                        vo.setContractEndDate(date);
                        vo.setContractEndDateContent(vo.getContractStartDateContent() + "，" + monthsContent);
                    } catch (Exception e) {
                        log.error("开始日期={}", startDate);
                        e.printStackTrace();
                    }
                } else if (StringUtils.hasLength(vo.getContractEndDate()) && StringUtils.isEmpty(vo.getContractStartDate())) {
                    String endDate = vo.getContractEndDate();
                    try {
                        if (endDate.contains("年") && endDate.contains("月") && !endDate.contains("日")) {
                            int index = endDate.indexOf("月");
                            if (index > -1) {
                                endDate = endDate.substring(0, index + 1);
                                endDate = endDate + "01日";
                            }
                        }
                        DateTime dateTime = DateUtil.parse(endDate);
                        DateTime startDate = DateUtil.offsetMonth(dateTime, -month);
                        String date = DateUtil.formatChineseDate(startDate, false, false);
                        vo.setContractStartDate(date);
                        vo.setContractStartDateContent(monthsContent + "，" + vo.getContractEndDateContent());
                    } catch (Exception e) {
                        log.error("结束日期={}", endDate);
                        e.printStackTrace();
                    }
                }
            }

            try {
//                for (PartyEntity entity1 : vo.getParty()) {
//                    parseAddress(entity1);
//                }
                economicResultMapper.insert(vo);
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
                    party.setAge(DateUtil.ageOfNow(DateUtil.parse(str)) + "");
                    party.setAgeContent(str);
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
   /*         if (!StringUtils.hasLength(party.getHasCriminalRecord())) {
                if (s.contains("刑满释放") || s.contains("因犯") || s.contains("曾因")) {
                    party.setHasCriminalRecord("是");
                }
            }*/
        }
        return party;
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


    public void address(EconomicTempVo entity, EconomicResultVo vo) {
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
