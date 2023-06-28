package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.service.divorce.DivorceMapper;
import com.ping.syncparse.service.divorce.DivorceVo;
import com.ping.syncparse.utils.ExcelUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ExportMsService {

    @Autowired
    private DivorceMapper divorceMapper;
    private int pageSize = 40000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    public void export() {
        pageNum.getAndIncrement();
        Criteria criteria = Criteria.where("name").regex("判决书");

        List<DivorceVo> vos = divorceMapper.findList(pageNum.get(), pageSize, null);
        String[] head = {"案件信息", "序号", "案件名称", "案号", "回溯一审案号", "法院名称", "裁判日期", "案由", "案件类型", "审判程序", "文书类型", "省份", "地市", "区县",
                "事实/审理查明", "判决结果", "理由", "法律依据", "诉讼记录", "HTML内容", "JSON内容", "相识年份", "相识月份", "相识时间", "相识时间内容", "相识方式", "相识方式内容",
                "是否订婚", "是否订婚内容", "订婚年份", "订婚月份", "订婚日期", "订婚日期内容", "是否办理结婚登记", "是否办理结婚登记内容", "办理结婚登记年份", "办理结婚登记月份", "办理结婚登记日期", "办理结婚登记内容",
                "是否举办婚礼", "是否举办婚礼内容", "举办婚礼年份", "举办婚礼月份", "举办婚礼日期", "举办婚礼日期内容",
                "是否同居", "是否同居内容", "解除关系年份", "解除关系月份", "解除关系日期", "解除关系内容", "是否有流产经历", "是否有流产经历内容", "是否有孩子", "是否有孩子内容",
                "彩礼数额", "彩礼数额内容", "彩礼是否包含首饰三金", "彩礼是否包含首饰三金内容", "彩礼是否包含汽车", "彩礼是否包含汽车内容", "彩礼是否包含房子", "彩礼是否包含房子内容",
                "彩礼来源", "彩礼去向", "是否提到生活困难", "是否提到生活困难内容", "是否提及负债", "是否提及负债内容", "判决彩礼返还金额", "判决彩礼返还金额内容"};

        List<Map<Integer, Object>> list = vos.parallelStream().map(this::toMap).collect(Collectors.toList());
        FileOutputStream out = null;
        try {
            File file = new File("E:\\导出\\婚约纠纷" + (pageNum.get() + 1) + ".xlsx");
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            List<Map<Integer, Object>> partyList = new ArrayList<>();
            List<String> partyHead = new ArrayList<>();
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
            partyHead.addAll(temp);
       /*      partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);
            partyHead.addAll(temp);*/
            for (DivorceVo vo : vos) {
                List<PartyEntity> party = vo.getParty();
                if (party != null) {
                    Map<String, List<PartyEntity>> listMap = party.stream().filter(c -> org.springframework.util.StringUtils.hasLength(c.getType())).collect(Collectors.groupingBy(PartyEntity::getType));
                    int start = 0;
                    int count = 0;
                    List<PartyEntity> entities = listMap.get("原告");
                    if (entities != null && entities.size() > 0) {
                        for (int i = 0; i < entities.size(); i++) {
                            PartyEntity entity = entities.get(i);
                            Map<Integer, Object> partyMap2 = new HashMap<>();
                            partyMap2.put(1, vo.getCaseNo());
                            toParty(partyMap2, entity);
                            partyList.add(partyMap2);
                            start += 13;
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
                            Map<Integer, Object> partyMap2 = new HashMap<>();
                            partyMap2.put(1, vo.getCaseNo());
                            toParty(partyMap2, entity);
                            partyList.add(partyMap2);
                            start += 13;
                            count++;
                        }
                    }


                } else {
                    partyList.add(new HashMap<>());
                }
            }
            SXSSFWorkbook workbook = null;
            Sheet sheet = null;
            Sheet partySheet = null;
            Sheet partySheet2 = null;
            XSSFWorkbook work = null;
            FileInputStream inputStream = new FileInputStream(file);

            workbook = new SXSSFWorkbook(1000);
            sheet = workbook.createSheet("案件信息");
            partySheet = workbook.createSheet("当事人信息");
       /*     if (pageNum.get() == 0) {
            workbook = new SXSSFWorkbook(1000);
            sheet = workbook.createSheet("案件信息");
            partySheet = workbook.createSheet("当事人信息");
            } else {
                work = new XSSFWorkbook(inputStream);
                workbook = new SXSSFWorkbook(work, 1000);
                sheet = workbook.getSheet("案件信息");
                partySheet = workbook.getSheet("当事人信息");
            }*/

            ExcelUtils.export(workbook, sheet, list, head);
            ExcelUtils.export(workbook, partySheet, partyList, partyHead.toArray());
      /*      if (pageNum.get() == 0) {
                ExcelUtils.export(workbook, sheet, list, head);
                ExcelUtils.export(workbook, partySheet, partyList, partyHead.toArray());
            } else {
                ExcelUtils.fillData(sheet, list, false);
                ExcelUtils.fillData(partySheet, partyList, false);
            }*/
            workbook.write(out);
            workbook.dispose();
            workbook.close();
            System.out.println("导出完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, Object> toCrime(int start, Map<Integer, Object> map, CrimeVO vo) {
        map.put(start + 2, vo.getName());
        map.put(start + 3, vo.getCrime());
        map.put(start + 4, vo.getImprisonmentTerm());
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

    private Map<Integer, Object> toMap(DivorceVo vo) {

        Map<Integer, Object> map = new HashMap<>();
        map.put(1, vo.getName());
        map.put(2, vo.getCaseNo());
        map.put(3, vo.getTId());
        map.put(4, vo.getCourtName());
        if (vo.getRefereeDate() != null) {
            map.put(5, DateUtil.format(vo.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            map.put(5, "");
        }
        map.put(6, vo.getCause());
        map.put(7, vo.getCaseType());
        map.put(8, vo.getTrialProceedings());
        map.put(9, vo.getDocType());
        map.put(10, vo.getProvince());
        map.put(11, vo.getCity());
        map.put(12, vo.getCounty());
        map.put(13, vo.getFact());
        map.put(14, vo.getJudgmentResult());
        map.put(15, vo.getCourtConsidered());
        map.put(16, vo.getLegalBasis());
        map.put(17, vo.getLitigationRecords());
        map.put(18, vo.getHtmlContent());
        if (vo.getJsonContent() != null) {
            map.put(19, vo.getJsonContent().toJSONString());
        } else {
            map.put(19, "");
        }
        map.put(20, getYear(vo.getKnowDate()));
        map.put(21, getMonth(vo.getKnowDate()));
        map.put(22, vo.getKnowDate());
        map.put(23, vo.getKnowDateContent());
        map.put(24, vo.getKnowWay());
        map.put(25, vo.getKnowWayContent());
        map.put(26, vo.getEngaged());
        map.put(27, vo.getEngagedContent());
        map.put(28, getYear(vo.getEngagedDate()));
        map.put(29, getMonth(vo.getEngagedDate()));
        map.put(30, vo.getEngagedDate());
        map.put(31, vo.getEngagedDateContent());
        map.put(32, vo.getMarriageRegistration());
        map.put(33, vo.getMarriageRegistrationContent());
        map.put(34, getYear(vo.getMarriageRegistrationDate()));
        map.put(35, getMonth(vo.getMarriageRegistrationDate()));
        map.put(36, vo.getMarriageRegistrationDate());
        map.put(37, vo.getMarriageRegistrationDateContent());
        map.put(38, vo.getHostingWedding());
        map.put(39, vo.getHostingWeddingContent());
        map.put(40, getYear(vo.getHostingWeddingDate()));
        map.put(41, getMonth(vo.getHostingWeddingDate()));
        map.put(42, vo.getHostingWeddingDate());
        map.put(43, vo.getHostingWeddingDateContent());
        map.put(44, vo.getLiveTogether());
        map.put(45, vo.getLiveTogetherContent());
        map.put(46, getYear(vo.getDissolveRelationshipDate()));
        map.put(47, getMonth(vo.getDissolveRelationshipDate()));
        map.put(48, vo.getDissolveRelationshipDate());
        map.put(49, vo.getDissolveRelationshipDateContent());
        map.put(50, vo.getAbort());
        map.put(51, vo.getAbortContent());
        map.put(52, vo.getChild());
        map.put(53, vo.getChildContent());
        try {
            if (vo.getBridePriceTotal() != null && vo.getBridePriceTotal().size() > 0) {
                int index = 1;
                StringBuilder total = new StringBuilder();
                for (String s : vo.getBridePriceTotal()) {
                    total.append(index).append("、").append(s).append("\r\n");
                    index++;
                }
                map.put(54, total.toString());
                StringBuilder totalContent = new StringBuilder();
                index = 1;
                for (String s : vo.getBridePriceTotalContent()) {
                    totalContent.append(index).append("、").append(s).append("\r\n");
                    index++;
                }
                map.put(55, totalContent.toString());
            } else {
                map.put(54, "");
                map.put(55, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put(56, vo.getBridePriceGold());
        map.put(57, vo.getBridePriceGoldContent());
        map.put(58, vo.getBridePriceCar());
        if (vo.getBridePriceCarContent() != null && vo.getBridePriceCarContent().size() > 0) {
            int index = 1;
            StringBuilder from = new StringBuilder();
            for (String s : vo.getBridePriceCarContent()) {
                from.append(index).append("、").append(s).append("\r\n");
                index++;
            }
            map.put(59, from.toString());
        } else {
            map.put(59, "");
        }

        map.put(60, vo.getBridePriceHouse());

        if (vo.getBridePriceHouseContent() != null && vo.getBridePriceHouseContent().size() > 0) {
            int index = 1;
            StringBuilder from = new StringBuilder();
            for (String s : vo.getBridePriceHouseContent()) {
                from.append(index).append("、").append(s).append("\r\n");
                index++;
            }
            map.put(61, from.toString());
        } else {
            map.put(61, "");
        }
        try {
            if (vo.getBridePriceFrom() != null && vo.getBridePriceFrom().size() > 0) {
                int index = 1;
                StringBuilder from = new StringBuilder();
                for (String s : vo.getBridePriceFrom()) {
                    from.append(index).append("、").append(s).append("\r\n");
                    index++;
                }
                map.put(62, from.toString());
            } else {
                map.put(62, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            if (vo.getBridePriceTo() != null && vo.getBridePriceTo().size() > 0) {
                int index = 1;
                StringBuilder to = new StringBuilder();
                for (String s : vo.getBridePriceFrom()) {
                    to.append(index).append("、").append(s).append("\r\n");
                    index++;
                }
                map.put(63, to.toString());
            } else {
                map.put(63, "");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put(64, vo.getBridePricePoverty());
        map.put(65, vo.getBridePricePovertyContent());
        map.put(66, vo.getBridePriceIndebted());
        map.put(67, vo.getBridePriceIndebtedContent());
        map.put(68, vo.getBridePriceReturn());
        if (StringUtils.hasLength(vo.getBridePriceReturnContent())) {
            map.put(69, vo.getBridePriceReturnContent());
            //   log.info("彩礼内容={}", vo.getBridePriceReturnContent());
        } else {
            map.put(69, "");
        }
        return map;

    }

    private String getYear(String content) {
        if (StringUtils.hasLength(content)) {
            int yearIndex = content.indexOf("年");
            if (yearIndex != -1) {
                try {
                    return content.substring(0, yearIndex);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String getMonth(String content) {
        if (StringUtils.hasLength(content)) {
            int yearIndex = content.indexOf("年");
            int monthIndex = content.indexOf("月");
            if (monthIndex != -1) {
                if (yearIndex > 0) {
                    try {
                        if (content.contains("正月")) {
                            return "正月";
                        }
                        if (content.contains("腊月")) {
                            return "腊月";
                        }
                        if (content.contains("元月")) {
                            return "元月";
                        }
                        return content.substring(yearIndex + 1, monthIndex);
                    } catch (Exception e) {
                        log.info("日期={}", content);
                        e.printStackTrace();
                        yearIndex = content.lastIndexOf("年");
                        monthIndex = content.lastIndexOf("月");
                        try {
                            return content.substring(yearIndex + 1, monthIndex);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    try {
                        if (content.contains("正月")) {
                            return "正月";
                        }
                        if (content.contains("正月")) {
                            return "正月";
                        }
                        if (content.contains("腊月")) {
                            return "腊月";
                        }
                        return content.substring(0, monthIndex);
                    } catch (Exception e) {
                        log.info("日期={}", content);
                        e.printStackTrace();
                        yearIndex = content.lastIndexOf("年");
                        monthIndex = content.lastIndexOf("月");
                        try {
                            return content.substring(0, monthIndex);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }

            }
        }
        return null;
    }

}
