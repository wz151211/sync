package com.ping.syncparse.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.utils.ExcelUtils;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ExportTempService {

    @Autowired
    private CaseXsMapper caseMapper;
    private int pageSize = 5000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    public void export() {
        pageNum.getAndIncrement();
        String[] head = {"案件信息", "序号", "案件名称", "案号", "法院名称", "立案日期", "裁判日期", "案由", "案件类型", "审判程序", "是否一审终审", "二审是否改判", "文书类型", "省份", "地市", "区县", "审理费", "法院层级", "是否使用简易程序", "是否适用小额诉讼", "租金", "利息", "违约金", "判决结果", "理由/法院认为", "法律依据", "诉讼记录", "事实", "HTML内容", "JSON内容", "二审案号"};
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

        IOUtils.setByteArrayMaxOverride(100000000 * 1000);
        ZipSecureFile.setMinInflateRatio(0.000001);
        String path = "E:\\导出\\刑事案件11.xlsx";
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
       //     Criteria criteria = Criteria.where("refereeDate").gte(DateUtil.parse("2018-01-01 00:00:00").toJdkDate()).lte(DateUtil.parse("2018-12-31 23:59:59").toJdkDate());

            long total = caseMapper.getCount(null);
            long page = 0;
            page = total % pageSize > 0 ? total / pageSize + 1 : total + pageSize;
            for (long index = 0; index < page; index++) {
                System.out.println("-----------" + index + "---------------");
                List<CaseVo> vos = caseMapper.findList((int) index, pageSize, null);
                if (vos.size() == 0) {
                    break;
                }
                SXSSFWorkbook workbook = null;
                Sheet sheet = null;
                Sheet partySheet = null;
                Sheet partySheet2 = null;
                XSSFWorkbook work = null;
                FileInputStream inputStream = new FileInputStream(file);
                if (index == 0) {
                    workbook = new SXSSFWorkbook(1000);
                    sheet = workbook.createSheet("案件信息");
                    partySheet = workbook.createSheet("当事人信息");
                    partySheet2 = workbook.createSheet("当事人信息2");
                } else {
                    work = new XSSFWorkbook(inputStream);
                    workbook = new SXSSFWorkbook(work, 1000);
                    sheet = workbook.getSheet("案件信息");
                    partySheet = workbook.getSheet("当事人信息");
                    partySheet2 = workbook.getSheet("当事人信息2");
                }
                List<Map<Integer, Object>> list = vos.parallelStream().map(this::toMap).collect(Collectors.toList());
                List<Map<Integer, Object>> partyList = new ArrayList<>();
                List<Map<Integer, Object>> partyList2 = new ArrayList<>();
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
                            for (int i = 0; i < entities.size(); i++) {
                                PartyEntity entity = entities.get(i);
                                toParty(start, partyMap, entity);
                                Map<Integer, Object> partyMap2 = new HashMap<>();
                                partyMap2.put(1, vo.getCaseNo());
                                toParty(partyMap2, entity);
                                partyList2.add(partyMap2);
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
                }
                if (index == 0) {
                    ExcelUtils.export(workbook, sheet, list, head);
                    ExcelUtils.export(workbook, partySheet, partyList, partyHead.toArray());
                    ExcelUtils.export(workbook, partySheet2, partyList2, partyHead2.toArray());
                } else {
                    ExcelUtils.fillData(sheet, list, false);
                    ExcelUtils.fillData(partySheet, partyList, false);
                    ExcelUtils.fillData(partySheet2, partyList2, false);
                }
                FileOutputStream out = new FileOutputStream(file);
                workbook.write(out);
                out.flush();
                workbook.close();
                workbook.dispose();
                out.close();
                inputStream.close();
            }
            System.out.println("导出完成");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
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
        CaseSummaryVO summaryVO = vo.getSummaryVO();
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, vo.getName());
        map.put(2, vo.getCaseNo());
        map.put(3, vo.getCourtName());

        if (vo.getRefereeDate() != null) {
            map.put(5, DateUtil.format(vo.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            map.put(5, "");
        }
        map.put(6, vo.getCause());
        map.put(7, vo.getCaseType());
        map.put(8, vo.getTrialProceedings());

        if (summaryVO != null) {
            map.put(4, summaryVO.getRegisterCaseDate());
            map.put(9, summaryVO.getFinalInstance());
            map.put(10, summaryVO.getChange());
            map.put(15, summaryVO.getHearingFees());
            map.put(16, summaryVO.getPrimary());
            map.put(17, summaryVO.getSummary());
            map.put(18, summaryVO.getSmall());
            map.put(19, summaryVO.getDisputedAmount());
            map.put(20, summaryVO.getDefaultInterest());
            map.put(21, summaryVO.getIquidatedDamages());
            map.put(29, summaryVO.getRetrialCaseNo());

        }


        map.put(11, vo.getDocType());
        map.put(12, vo.getProvince());
        map.put(13, vo.getCity());
        map.put(14, vo.getCounty());


        map.put(22, vo.getJudgmentResult());
        map.put(23, vo.getCourtConsidered());
        map.put(24, vo.getLegalBasis());
        map.put(25, vo.getLitigationRecords());
        map.put(26, vo.getFact());
        map.put(27, vo.getHtmlContent());
        if (vo.getJsonContent() != null) {
            map.put(28, vo.getJsonContent());
        } else {
            map.put(28, "");
        }
        return map;

    }

}
