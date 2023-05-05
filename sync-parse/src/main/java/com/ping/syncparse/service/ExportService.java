package com.ping.syncparse.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.CollectionUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ExportService {

    @Autowired
    private CaseXsMapper caseMapper;
    private int pageSize = 10000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    public void export() {
        pageNum.getAndIncrement();
        List<CaseVo> vos = caseMapper.findList(pageNum.get(), pageSize, null);
        Workbook wb = new XSSFWorkbook();
        String[] head = {"案件信息", "序号", "诉讼主体", "诉讼地位", "相关当事人", "案件名称", "案号", "法院名称", "裁判日期", "案由", "案件类型", "审判程序", "文书类型", "省份", "地市", "区县",
                "原告诉讼请求", "事实/审理查明", "判决结果", "判决结果", "执行结果", "涉及金额", "理由", "法律依据", "诉讼记录", "HTML内容", "JSON内容"};
        Sheet sheet = wb.createSheet("案件信息");
        List<Map<Integer, Object>> list = vos.parallelStream().map(this::toMap).collect(Collectors.toList());
        FileOutputStream out = null;
        try {
            File file = new File("E:\\导出\\刑事案件.xlsx");
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            ExcelUtils.export(wb, sheet, list, head);
            List<Map<Integer, Object>> partyList = new ArrayList<>();
            List<Map<Integer, Object>> partyList2 = new ArrayList<>();
            List<Map<Integer, Object>> crimeList = new ArrayList<>();
            List<Map<Integer, Object>> crimeList2 = new ArrayList<>();
            List<Map<Integer, Object>> parseResult = new ArrayList<>();

            List<String> partyHead = new ArrayList<>();
            List<String> partyHead2 = new ArrayList<>();
            partyHead.add("当事人信息");
            partyHead.add("序号");
            partyHead.add("案号");
            List<String> temp = new ArrayList<>();
            temp.add("类型");
            temp.add("姓名");
            temp.add("性别");
            temp.add("年龄");
            temp.add("出生日期");
            temp.add("民族");
            temp.add("省份");
            temp.add("地市");
            temp.add("区县");
            temp.add("地址");
            temp.add("文化水平");
            temp.add("职业");
            temp.add("内容");
            CollectionUtil.addAllIfNotContains(partyHead2, partyHead);
            CollectionUtil.addAllIfNotContains(partyHead2, temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            List<String> crimeHead = new ArrayList<>();
            List<String> crimeHead2 = new ArrayList<>();
            crimeHead.add("判决结果");
            crimeHead.add("序号");
            crimeHead.add("案号");
            List<String> t = new ArrayList<>();
            t.add("姓名");
            t.add("罪名");
            t.add("刑期");
            CollectionUtil.addAllIfNotContains(crimeHead2, crimeHead);
            CollectionUtil.addAllIfNotContains(crimeHead2, t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);
            crimeHead.addAll(t);


            for (CaseVo vo : vos) {
                List<PartyEntity> party = vo.getParty();
                if (party != null) {
                    Map<String, List<PartyEntity>> listMap = party.stream().filter(c -> StringUtils.hasLength(c.getType())).collect(Collectors.groupingBy(PartyEntity::getType));
                    Map<Integer, Object> partyMap = new HashMap<>();
                    partyMap.put(1, vo.getCaseNo());
                    partyList.add(partyMap);
                    int start = 0;
                    int count = 0;
                    List<PartyEntity> entities = listMap.get("原告");
                    if (entities != null && entities.size() > 0) {
                        for (int i = 0; i < 3; i++) {
                            PartyEntity entity = null;
                            if (i < entities.size()) {
                                entity = entities.get(i);
                            } else {
                                entity = new PartyEntity();
                            }
                            toParty(start, partyMap, entity);
                            start += 9;
                            count++;
                        }
                    } else {
                        for (int i = 0; i < 3; i++) {
                            PartyEntity entity = null;

                            entity = new PartyEntity();

                            toParty(start, partyMap, entity);
                            start += 9;
                            count++;
                        }
                    }

                    List<PartyEntity> bList = listMap.get("被告");
                    if (bList != null && bList.size() > 0) {
                        for (int i = 0; i < bList.size(); i++) {
                            if (count >= 10) {
                                break;
                            }
                            PartyEntity entity = bList.get(i);
                            toParty(start, partyMap, entity);

                            Map<Integer, Object> partyMap2 = new HashMap<>();
                            partyMap2.put(1, vo.getCaseNo());
                            toParty(partyMap2, entity);
                            partyList2.add(partyMap2);

                            start += 13;
                            count++;
                        }
                    }


                } else {
                    partyList.add(new HashMap<>());
                    partyList2.add(new HashMap<>());
                }
                List<CrimeVO> crimes = vo.getCrimes();
                if (crimes != null) {
                    Map<Integer, Object> crimeMap = new HashMap<>();
                    crimeMap.put(1, vo.getCaseNo());
                    crimeList.add(crimeMap);
                    int start = 0;
                    for (int i = 0; i < crimes.size(); i++) {
                        if (i >= 10) {
                            break;
                        }
                        CrimeVO crimeVO = crimes.get(i);
                        toCrime(start, crimeMap, crimeVO);
                        start += 3;

                        Map<Integer, Object> crimeMap2 = new HashMap<>();
                        crimeMap2.put(1, vo.getCaseNo());
                        toCrime(0, crimeMap2, crimeVO);
                        crimeList2.add(crimeMap2);
                    }
                } else {
                    crimeList.add(new HashMap<>());
                }
            }
            Sheet partySheet = wb.createSheet("当事人信息");
            ExcelUtils.export(wb, partySheet, partyList, partyHead.toArray());

            Sheet partySheet2 = wb.createSheet("当事人信息2");
            ExcelUtils.export(wb, partySheet2, partyList2, partyHead2.toArray());

     /*       Sheet crimeSheet = wb.createSheet("判决结果");
            ExcelUtils.export(wb, crimeSheet, crimeList, crimeHead.toArray());

            Sheet crimeSheet2 = wb.createSheet("判决结果2");
            ExcelUtils.export(wb, crimeSheet2, crimeList2, crimeHead2.toArray());*/
            wb.write(out);
            System.out.println("导出完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                wb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private Map<Integer, Object> toCrime(int start, Map<Integer, Object> map, CrimeVO vo) {
        map.put(start + 2, vo.getName());
        map.put(start + 3, vo.getCrime());
        map.put(start + 4, vo.getImprisonmentTerm());
        return map;

    }


    private Map<Integer, Object> toParty(int start, Map<Integer, Object> map, PartyEntity party) {
        map.put(start + 2, party.getType());
        map.put(start + 3, party.getName());
        map.put(start + 4, party.getSex());
        map.put(start + 5, party.getAge());
        map.put(start + 6, party.getBirthday());
        map.put(start + 7, party.getNation());
        map.put(start + 8, party.getProvince());
        map.put(start + 9, party.getCity());
        map.put(start + 10, party.getCounty());
        map.put(start + 11, party.getAddress());
        map.put(start + 12, party.getEduLevel());
        map.put(start + 13, party.getProfession());
        map.put(start + 14, party.getContent());
        return map;

    }

    private Map<Integer, Object> toParty(Map<Integer, Object> map, PartyEntity party) {
        map.put(2, party.getType());
        map.put(3, party.getName());
        map.put(4, party.getSex());
        map.put(5, party.getAge());
        map.put(6, party.getBirthday());
        map.put(7, party.getNation());
        map.put(8, party.getProvince());
        map.put(9, party.getCity());
        map.put(10, party.getCounty());
        map.put(11, party.getAddress());
        map.put(12, party.getEduLevel());
        map.put(13, party.getProfession());
        map.put(14, party.getContent());
        return map;

    }

    private Map<Integer, Object> toMap(CaseVo vo) {
        Map<Integer, Object> map = new HashMap<>();
        Map<String, List<PartyEntity>> listMap = vo.getParty().stream().filter(c -> StringUtils.hasLength(c.getType())).collect(Collectors.groupingBy(PartyEntity::getType));
        List<PartyEntity> yg = listMap.get("原告");
        List<PartyEntity> bg = listMap.get("被告");
        String name = "";
        String name1 = "";
        if (yg != null) {
            name = yg.stream().map(PartyEntity::getName).collect(Collectors.joining("、"));
        }
        if (bg != null) {
            name1 = bg.stream().map(PartyEntity::getName).collect(Collectors.joining("、"));
        }

        map.put(1, name);

        if (name.contains("中汇电子支付有限公司")) {
            map.put(2, "原告");
        }
        if (name1.contains("中汇电子支付有限公司")) {
            map.put(2, "被告");
        }

        String[] head = {"案件信息", "序号", "诉讼主体", "诉讼地位", "相关当事人", "案件名称", "案号", "法院名称", "裁判日期", "案由", "案件类型", "审判程序", "文书类型", "省份", "地市", "区县",
                "原告诉讼请求", "事实/审理查明", "判决结果", "判决结果", "执行结果", "涉及金额", "理由", "法律依据", "诉讼记录", "HTML内容", "JSON内容"};
        map.put(3, name1);
        map.put(4, vo.getName());
        map.put(5, vo.getCaseNo());
        map.put(6, vo.getCourtName());
        if (vo.getRefereeDate() != null) {
            map.put(7, DateUtil.format(vo.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            map.put(7, "");
        }
        map.put(8, vo.getCause());
        map.put(9, vo.getCaseType());
        map.put(10, vo.getTrialProceedings());
        map.put(11, vo.getDocType());
        map.put(12, vo.getProvince());
        map.put(13, vo.getCity());
        map.put(14, vo.getCounty());
        map.put(15, vo.getLitigationClaims());
        map.put(16, vo.getFact());
        map.put(17, vo.getJudgmentDesc());
        map.put(18, vo.getJudgmentResult());
        map.put(19, vo.getExecutionResult());
        StringBuilder money = new StringBuilder();
        for (int i = 0; i < vo.getMoney().size(); i++) {
            String s = vo.getMoney().get(i);
            money.append(i + 1).append("、").append(s).append("\r\n");
        }
        map.put(20, money.toString());
        map.put(21, vo.getCourtConsidered());
        map.put(22, vo.getLegalBasis());
        map.put(23, vo.getLitigationRecords());
        map.put(24, vo.getHtmlContent());
        if (vo.getJsonContent() != null) {
            map.put(25, vo.getJsonContent());
        } else {
            map.put(25, "");
        }
/*        StringBuilder money = new StringBuilder();
        int a = 1;
        for (String s : vo.getMoney()) {
            money.append(a).append("、").append(s).append("\r\n");
            a++;
        }
        StringBuilder moneyString = new StringBuilder();
        int b = 1;
        for (String s : vo.getMoneyString()) {
            moneyString.append(b).append("、").append(s).append("\r\n");
            b++;
        }
        map.put(20, money.toString());
        map.put(21, moneyString.toString());*/

        return map;

    }

}
