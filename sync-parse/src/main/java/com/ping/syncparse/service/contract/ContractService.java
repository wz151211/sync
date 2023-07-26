package com.ping.syncparse.service.contract;

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

import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public class ContractService {

    @Autowired
    private ContractTempMapper contractTempMapper;
    @Autowired
    private ContractResultMapper contractResultMapper;
    @Autowired
    private AreaService areaService;
    @Autowired
    private PartyMapper partyMapper;

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
        List<ContractTempVo> entities = contractTempMapper.findList(pageNum.get(), pageSize, null);
        if (entities == null || entities.size() == 0) {
            return;
        }
        pageNum.getAndIncrement();
        entities.parallelStream().forEach(entity -> {
            ContractResultVo vo = new ContractResultVo();
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
            vo.setCause(entity.getCause());
            vo.setLegalBasis(entity.getLegalBasis());
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
                fact = entity.getLitigationRecords() + fact;

            }
            String startFact = "";
            if (StringUtils.hasLength(fact)) {
                int index = fact.indexOf("事实和理由");
                if (index == -1) {
                    index = fact.indexOf("查明的事实");
                }
                if (index == -1) {
                    index = fact.indexOf("事实与理由");
                }
                if (index == -1) {
                    index = fact.indexOf("审理认定");
                }
                if (index == -1) {
                    index = fact.indexOf("审理查明");
                }
                if (index > 0) {
                    startFact = fact.substring(0, index);
                    fact = fact.substring(index);
                }
                for (String sentence : fact.split("。")) {
                    sentence = sentence.replace(";", "，");
                    sentence = sentence.replace("；", "，");
                    String[] split = sentence.split("，");
                    for (int i = 0; i < split.length; i++) {
                        String comma = split[i];
                        comma = comma.replace("Ｏ", "0");
                        comma = comma.replace("ｌ", "0");
                        comma = comma.replace(" ", "");
                        if (((comma.contains("签订") || comma.contains("签署")) && (comma.contains("合同") || comma.contains("合约") || comma.contains("协议"))) || comma.contains("合同名称") || comma.contains("合同签订时间")) {
                            String temp = "";
                            if (i > 0) {
                                temp = split[i - 1] + comma;
                            }
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
                                if (realName.contains("个")) {
                                    continue;
                                }
                                if (realName.contains("年") && realName.length() < 5) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    contractSigningDate += term.getRealName();
                                }
                            }
                            if (StringUtils.isEmpty(vo.getContractSigningDate()) && StringUtils.hasLength(contractSigningDate)) {
                                vo.setContractSigningDate(contractSigningDate);
                                vo.setContractSigningDateContent(temp);
                            }
                            int start = comma.indexOf("《");
                            int end = comma.indexOf("》");
                            if (comma.contains("《") && comma.contains("》") && end > start) {
                                try {
                                    String name = comma.substring(start, end + 1);
                                    name = name.replace("了", "");
                                    name = name.replace("的", "");
                                    //  log.info("合同名称={}", name);
                                    if (StringUtils.isEmpty(vo.getContractName())) {
                                        vo.setContractName(name);
                                        vo.setContractNameContent(comma);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                try {
                                    start = comma.indexOf("签订");
                                    end = comma.indexOf("合同");
                                    if (start == -1) {
                                        comma.indexOf("签署");
                                    }
                                    if (end == -1) {
                                        end = comma.indexOf("合约");
                                    }
                                    if (end == -1) {
                                        end = comma.indexOf("协议");
                                    }
                                    if (end > start) {
                                        String name = comma.substring(start + 2, end + 2);
                                        name = name.replace("了", "");
                                        name = name.replace("的", "");
                                        // log.info("合同名称2={}", name);
                                        if (StringUtils.isEmpty(vo.getContractName())) {
                                            vo.setContractName(name);
                                            vo.setContractNameContent(sentence);
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                        if ((comma.contains("授信额度")
                                || comma.contains("贷款")
                                || comma.contains("额度")
                                || comma.contains("欠款")
                                || comma.contains("化肥款")
                                || comma.contains("借款")
                                || comma.contains("累计欠款")
                                || comma.contains("分期资金")
                                || comma.contains("透支款本金")
                                || comma.contains("透支本金")
                                || comma.contains("欠款本金")
                                || comma.contains("透支金额")
                                || comma.contains("货款总额")
                                || comma.contains("消费共计")
                                || comma.contains("车款")
                                || comma.contains("支用")
                                || comma.contains("本金"))
                                && !comma.contains("不超过")
                                && !comma.contains("判令")
                                && !comma.contains("冲抵")
                                && !comma.contains("归还")
                                && StringUtils.isEmpty(vo.getLoanAmount())) {
                            comma = comma.replace("？", "0");
                            comma = comma.replace(",", "");
                            Result parse = ToAnalysis.parse(comma);
                            for (Term term : parse.getTerms()) {
                                if (term.getRealName().contains("元") && term.getNatureStr().equals("mq")) {
                                    if (StringUtils.isEmpty(vo.getLoanAmount())) {
                                        vo.setLoanAmount(term.getRealName());
                                        vo.setLoanAmountContent(comma);
                                    }
                                }
                                if (term.from() != null && term.from().getRealName().contains("借款") && term.getNatureStr().equals("m")) {
                                    if (StringUtils.isEmpty(vo.getLoanAmount())) {
                                        vo.setLoanAmount(term.getRealName());
                                        vo.setLoanAmountContent(comma);
                                    }
                                }
                            }
                        }

                        if (comma.contains("期限") && (comma.contains("至") || (comma.contains("起") && comma.contains("到"))) && comma.contains("年") && comma.contains("月") && comma.contains("日") && !comma.contains("截止")) {
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
                                        if (realName.contains("个")) {
                                            continue;
                                        }
                                        if (realName.contains("年") && realName.length() < 5) {
                                            continue;
                                        }
                                        if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                            contractStartDate += term.getRealName();
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
                                        if (realName.contains("个")) {
                                            continue;
                                        }
                                        if (realName.contains("年") && realName.length() < 5) {
                                            continue;
                                        }
                                        if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                            contractEndDate += term.getRealName();
                                        }
                                    }
                                } catch (Exception e) {
                                    log.info("起止日期={}", sentence);
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

                        if (comma.contains("借款") && comma.contains("年") && comma.contains("月") && comma.contains("日") && StringUtils.isEmpty(vo.getContractStartDate())) {
                            String contractStartDate = "";
                            for (Term term : ToAnalysis.parse(comma)) {
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
                                if (realName.contains("个")) {
                                    continue;
                                }
                                if (realName.contains("年") && realName.length() < 5) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    contractStartDate += term.getRealName();
                                }
                            }
                            if (StringUtils.isEmpty(vo.getContractStartDate()) && StringUtils.hasLength(contractStartDate)) {
                                vo.setContractStartDate(contractStartDate);
                                vo.setContractStartDateContent(comma);
                            }
                        }
                        if ((comma.contains("还清") || comma.contains("到期")) && comma.contains("年") && comma.contains("月") && comma.contains("日") && StringUtils.isEmpty(vo.getContractEndDate())) {
                            String contractEndDate = "";
                            for (Term term : ToAnalysis.parse(comma)) {
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
                                if (realName.contains("个")) {
                                    continue;
                                }
                                if (realName.contains("年") && realName.length() < 5) {
                                    continue;
                                }
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    contractEndDate += term.getRealName();
                                }
                            }
                            if (StringUtils.isEmpty(vo.getContractEndDate()) && StringUtils.hasLength(contractEndDate)) {
                                vo.setContractEndDate(contractEndDate);
                                vo.setContractEndDateContent(comma);
                            }
                        }
                        if ((comma.contains("基准利率")
                                || comma.contains("贷款利率")
                                || comma.contains("年利率")
                                || comma.contains("月利率")
                                || comma.contains("月息")
                                || comma.contains("月利")
                                || comma.contains("日利率"))
                                && (comma.contains("%") || comma.contains("‰") || comma.contains("分")) && !comma.contains("不超过") && !comma.contains("罚息") && StringUtils.isEmpty(vo.getLoanRate())) {
                            for (Term term : ToAnalysis.parse(comma)) {
                                if (StringUtils.isEmpty(vo.getLoanRate())) {
                                    if (term.getNatureStr().equals("mq") && !term.getRealName().contains("元")) {
                                        vo.setLoanRate(term.getRealName());
                                        vo.setLoanRateContent(comma);
                                    }
                                    if (term.getNatureStr().equals("m") && term.to() != null && term.to().getRealName().equals("‰")) {
                                        vo.setLoanRate(term.getRealName() + term.to().getRealName());
                                        vo.setLoanRateContent(comma);
                                    }
                                }
                            }
                            if (StringUtils.isEmpty(vo.getLoanRate()) && comma.contains("万分之五")) {
                                vo.setLoanRate("0.05%");
                                vo.setLoanRateContent(comma);
                            }
                        }
                        if ((comma.contains("逾期") || comma.contains("罚息") || comma.contains("违约利率")) && (comma.contains("%") || comma.contains("‰")) && StringUtils.isEmpty(vo.getOverdueRate())) {
                            for (Term term : ToAnalysis.parse(comma)) {
                                if (term.getNatureStr().equals("mq") && !term.getRealName().contains("元")) {
                                    vo.setOverdueRate(term.getRealName());
                                }
                                if (term.getNatureStr().equals("m") && term.to() != null && term.to().getRealName().equals("‰")) {
                                    vo.setOverdueRate(term.getRealName() + term.to().getRealName());
                                }
                            }
                            vo.setOverdueRateContent(comma);
                        }
                        if ((comma.contains("抵押物")
                                || comma.contains("为抵押")
                                || comma.contains("抵押给")
                                || comma.contains("提供抵押")
                                || comma.contains("设定抵押")
                                || comma.contains("保证担保")
                                || comma.contains("设立抵押")
                                || comma.contains("抵押担保")
                                || comma.contains("贷款抵押")
                                || comma.contains("贷款担保")
                                || comma.contains("进行抵押")
                                || comma.contains("作抵押")
                                || comma.contains("作了抵押")
                                || (comma.contains("办理") && comma.contains("抵押"))
                                || comma.contains("作为抵押")
                                || comma.contains("抵押车辆信息"))
                                && (comma.contains("房") || comma.contains("车") || comma.contains("商铺") || comma.contains("机") || comma.contains("土地使用证"))
                                && StringUtils.isEmpty(vo.getMortgage())) {
                            vo.setMortgage(sentence);
                        }

                        if ((comma.contains("未还款") || comma.contains("罚息") || comma.contains("逾期")) && comma.contains("年") && comma.contains("月") && comma.contains("日") && !comma.contains("止") && StringUtils.isEmpty(vo.getDefaultDate())) {
                            String defaultDate = "";
                            for (Term term : ToAnalysis.parse(comma)) {
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
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    defaultDate += term.getRealName();
                                }
                            }
                            if (StringUtils.isEmpty(vo.getDefaultDate())) {
                                vo.setDefaultDate(defaultDate);
                                vo.setDefaultDateContent(comma);
                            }
                        }
                    }
                }
            }
            if (StringUtils.hasLength(startFact) && StringUtils.isEmpty(vo.getLoanAmount())) {
                startFact = startFact + entity.getJudgmentResult();
                startFact.replace(";", "，");
                startFact.replace("；", "，");
                startFact.replace("。", "，");
                String[] split = startFact.split("，");
                for (String comma : split) {
                    if ((comma.contains("授信额度")
                            || comma.contains("额度")
                            || comma.contains("贷款")
                            || comma.contains("欠款")
                            || comma.contains("借款")
                            || comma.contains("累计欠款")
                            || comma.contains("分期资金")
                            || comma.contains("透支款本金")
                            || comma.contains("透支本金")
                            || comma.contains("信用卡本金")
                            || comma.contains("车款")
                            || comma.contains("透支金额")
                            || comma.contains("本金")
                            || comma.contains("欠款本金")) && !comma.contains("不超过") && StringUtils.isEmpty(vo.getLoanAmount())) {
                        Result parse = ToAnalysis.parse(comma);
                        for (Term term : parse.getTerms()) {
                            if (term.getRealName().contains("元") && term.getNatureStr().equals("mq")) {
                                if (StringUtils.isEmpty(vo.getLoanAmount())) {
                                    vo.setLoanAmount(term.getRealName());
                                    vo.setLoanAmountContent(comma);
                                }
                            }
                        }
                    }
                }
            }

            String judgmentResult = entity.getJudgmentResult();
            if (StringUtils.hasLength(judgmentResult)) {
                judgmentResult = judgmentResult.replace(";", "。");
                judgmentResult = judgmentResult.replace("；", "。");
                for (String judgment : judgmentResult.split("。")) {
                    if ((judgment.contains("本金") || judgment.contains("借款") || judgment.contains("贷款") || judgment.contains("车款")) && judgment.contains("元") && StringUtils.isEmpty(vo.getDefaultAmount())) {
                        for (Term term : ToAnalysis.parse(judgment)) {
                            if (term.getNatureStr().equals("mq") && term.getRealName().contains("元")) {
                                if (StringUtils.isEmpty(vo.getDefaultAmount())) {
                                    vo.setDefaultAmount(term.getRealName());
                                    vo.setDefaultAmountContent(judgment);
                                }
                            } else if (term.getNatureStr().equals("m") && term.from() != null && (term.from().getRealName().contains("人民币"))) {
                                if (StringUtils.isEmpty(vo.getDefaultAmount())) {
                                    vo.setDefaultAmount(term.getRealName());
                                    vo.setDefaultAmountContent(judgment);
                                }
                            }
                        }
                    }
                    String[] split = judgment.split("，");
                    for (String comma : split) {
                        if ((comma.contains("计算利息") || comma.contains("逾期利息")) && comma.contains("起") && comma.contains("年") && comma.contains("月") && comma.contains("日") && StringUtils.isEmpty(vo.getDefaultDate())) {
                            String defaultDate = "";
                            for (Term term : ToAnalysis.parse(comma)) {
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
                                if (term.getNatureStr().equals("t") && (realName.contains("年") || realName.contains("月") || realName.contains("日"))) {
                                    if (StringUtils.isEmpty(defaultDate) && realName.contains("年")) {
                                        defaultDate += term.getRealName();
                                    } else if (StringUtils.hasLength(defaultDate) && (realName.contains("月") || realName.contains("日"))) {
                                        defaultDate += term.getRealName();
                                    }
                                }
                            }
                            if (StringUtils.isEmpty(vo.getDefaultDate())) {
                                vo.setDefaultDate(defaultDate);
                                vo.setDefaultDateContent(comma);
                            }
                        }
                    }

                }
            }
            try {
                for (PartyEntity entity1 : vo.getParty()) {
                    parseAddress(entity1);
                }
                vo.setParty(null);
                vo.setParty(entity.getParty());
                contractResultMapper.insert(vo);
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


    public void address(ContractTempVo entity, ContractResultVo vo) {
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
        illness.add("酒精所致的精神和行为障碍");
        illness.add("酒精所致精神障碍");
        illness.add("复发性抑郁症");
        illness.add("酒精中毒所致精神障碍");
        illness.add("未特定的精神障碍");
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

        illness.add("边缘智力伴精神障碍");
        illness.add("情感性精神障碍");
        illness.add("急性而短暂的精神病性障碍");
        illness.add("急性而短暂的精神病性症");
        illness.add("急性而短暂的××性障碍");
        illness.add("急性短暂性精神障碍");
        illness.add("急性短暂性精神病");
        illness.add("急性应激性精神病");
        illness.add("酒精所致精神和行为障碍");
        illness.add("酒精所致幻觉症");
        illness.add("脑器质性疾病所致精神障碍");
        illness.add("待分类的其他精神障碍");
        illness.add("心境障碍");
        illness.add("被害妄想症");
        illness.add("被害妄想");
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
        illness.add("偏执型精神分裂症");
        illness.add("癲痫所致精神障碍");
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
        //    illness.add("分裂症");
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
        illness.add("病理性半醒");
        illness.add("酒精所致××障碍");
        illness.add("继发性痴呆");
        illness.add("重度抑郁");
        illness.add("精神分裂症");
        illness.add("病理性醉酒");
        illness.add("癫痫性精神病");
        illness.add("情感性精神障碍");
        illness.add("器质性精神障碍");
        illness.add("癫痫");
        illness.add("偏执型精神分裂症");
        illness.add("妄想状态");
        //    illness.add("精神障碍");
    }

}
