package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import com.ping.syncparse.utils.ExcelUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
public class ExportXsService {

    @Autowired
    private CaseXsMapper caseMapper;
    private int pageSize = 40000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    @Autowired
    private DocumentXsMapper documentXsMapper;

    public void export() {
        pageNum.getAndIncrement();
        List<CaseVo> vos = caseMapper.findList(pageNum.get(), pageSize, null);
        Workbook wb = new XSSFWorkbook();
        String[] head = {"案件信息", "序号", "案件名称", "案号", "法院名称", "裁判日期", "案由", "案件类型", "审判程序", "文书类型", "省份", "地市", "区县",
                "事实/审理查明", "判决结果", "理由", "法律依据", "诉讼记录", "HTML内容", "JSON内容"};
        Sheet sheet = wb.createSheet("案件信息");
        List<Map<Integer, Object>> list = vos.parallelStream().map(this::toMap).collect(Collectors.toList());
        FileOutputStream out = null;
        try {
            File file = new File("E:\\导出\\侵害实用新型专利权纠纷-" + (pageNum.get() + 1) + ".xlsx");
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            ExcelUtils.export(wb, sheet, list, head);
            List<String> partyHead = new ArrayList<>();
            partyHead.add("当事人信息");
            partyHead.add("序号");
            partyHead.add("案号");
            partyHead.add("类型");
            partyHead.add("姓名");
            // partyHead.add("性别");
            // partyHead.add("年龄");
            // partyHead.add("出生日期");
            // partyHead.add("民族");
            // partyHead.add("省份");
            // partyHead.add("地市");
            // partyHead.add("区县");
            // partyHead.add("地址");
            // partyHead.add("文化水平");
            // partyHead.add("职业");
            partyHead.add("内容");

            List<String> crimeHead = new ArrayList<>();
            crimeHead.add("判决结果");
            crimeHead.add("序号");
            crimeHead.add("案号");
            crimeHead.add("名称");
            crimeHead.add("是否单位犯罪");
            crimeHead.add("文化程度");
            crimeHead.add("经审理查明");
            crimeHead.add("罪名");
            crimeHead.add("刑期");
            crimeHead.add("是否双罚制");
            crimeHead.add("是否缓刑");
            crimeHead.add("内容");

            List<Map<Integer, Object>> partyList = new ArrayList<>();
            List<Map<Integer, Object>> crimeList = new ArrayList<>();

            for (CaseVo vo : vos) {
                List<PartyEntity> party = vo.getParty();
                if (party != null) {
                    Map<String, List<PartyEntity>> listMap = party.stream().filter(c -> org.springframework.util.StringUtils.hasLength(c.getType())).collect(Collectors.groupingBy(PartyEntity::getType));
                    List<PartyEntity> entities = listMap.get("原告");
                    if (entities != null && entities.size() > 0) {
                        for (int i = 0; i < entities.size(); i++) {
                            PartyEntity entity = entities.get(i);
                            entity.setAge("");
                            try {
                                if (StringUtils.hasLength(entity.getAgeContent())) {
                                    entity.setAge(DateUtil.age(DateUtil.parse(entity.getAgeContent()), vo.getRefereeDate()) + "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Map<Integer, Object> tempMap = new HashMap<>();
                            tempMap.put(1, vo.getCaseNo());
                            toParty(tempMap, entity);
                            partyList.add(tempMap);
                        }
                    }

                    List<PartyEntity> bList = listMap.get("被告");
                    if (bList != null && bList.size() > 0) {
                        for (int i = 0; i < bList.size(); i++) {
                            PartyEntity entity = bList.get(i);
                            entity.setAge("");
                            try {
                                if (StringUtils.hasLength(entity.getAgeContent())) {
                                    entity.setAge(DateUtil.age(DateUtil.parse(entity.getAgeContent()), vo.getRefereeDate()) + "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Map<Integer, Object> tempMap = new HashMap<>();
                            tempMap.put(1, vo.getCaseNo());
                            toParty(tempMap, entity);
                            partyList.add(tempMap);
                        }
                    }


                }

                List<CrimeVO> crimes = vo.getCrimes();
                if (crimes != null) {
                    for (int i = 0; i < crimes.size(); i++) {
                        Map<Integer, Object> crimeMap = new HashMap<>();
                        CrimeVO crimeVO = crimes.get(i);
                        crimeMap.put(1, vo.getCaseNo());
                        toCrime(crimeMap, crimeVO);
                        crimeList.add(crimeMap);
                    }
                }
            }

            Sheet partySheet2 = wb.createSheet("当事人信息");
            ExcelUtils.export(wb, partySheet2, partyList, partyHead.toArray());

           // Sheet crimeSheet = wb.createSheet("判决结果");
           // ExcelUtils.export(wb, crimeSheet, crimeList, crimeHead.toArray());
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
 /*       String target = "G:\\保险诈骗\\";
        for (DocumentXsLhEntity vo : vos) {
            String docPath = target + FilenameUtils.getBaseName(vo.getName()) + ".docx";
            File docFile = new File(docPath);
            if (docFile.exists()) {
                docPath = target + FilenameUtils.getBaseName(vo.getName()) + "-" + RandomUtil.randomString(5) + ".docx";
            }
            htmlAsAltChunk2Docx(vo.getHtmlContent(), docPath);
        }*/
    }


    private Map<Integer, Object> toCrime(int start, Map<Integer, Object> map, CrimeVO vo) {
        map.put(start + 2, vo.getName());
        map.put(start + 3, vo.getCrime());
        map.put(start + 4, vo.getImprisonmentTerm());
        return map;

    }

    private Map<Integer, Object> toCrime(Map<Integer, Object> map, CrimeVO vo) {
        map.put(2, vo.getName());
        map.put(3, vo.getUnitCrime());
        map.put(4, vo.getEduLevel());
        map.put(5, vo.getFact());
        map.put(6, vo.getCrime());
        map.put(7, vo.getImprisonmentTerm());
        map.put(8, vo.getDoublePenalty());
        map.put(9, vo.getIsProbation());
        map.put(10, vo.getContent());
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
        // map.put(2, party.getType());
        // map.put(3, party.getName());
        // map.put(4, party.getSex());
        // map.put(5, party.getAge());
        // map.put(6, party.getBirthday());
        // map.put(7, party.getNation());
        // map.put(8, party.getProvince());
        // map.put(9, party.getCity());
        // map.put(10, party.getCounty());
        // map.put(11, party.getAddress());
        // map.put(12, party.getEduLevel());
        // map.put(13, party.getProfession());
        // map.put(14, party.getContent());

        map.put(2, party.getType());
        map.put(3, party.getName());
        map.put(4, party.getContent());
        return map;

    }

    private Map<Integer, Object> toMap(CaseVo vo) {
        Map<Integer, Object> map = new HashMap<>();
        map.put(1, vo.getName());
        map.put(2, vo.getCaseNo());
        map.put(3, vo.getCourtName());
        if (vo.getRefereeDate() != null) {
            map.put(4, DateUtil.format(vo.getRefereeDate(), DateTimeFormatter.ISO_LOCAL_DATE));
        } else {
            map.put(4, "");
        }
        map.put(5, vo.getCause());
        map.put(6, vo.getCaseType());
        map.put(7, vo.getTrialProceedings());
        map.put(8, vo.getDocType());
        map.put(9, vo.getProvince());
        map.put(10, vo.getCity());
        map.put(11, vo.getCounty());
        map.put(12, vo.getFact());
        map.put(13, vo.getJudgmentResult());
        map.put(14, vo.getCourtConsidered());
        map.put(15, vo.getLegalBasis());
        map.put(16, vo.getLitigationRecords());
        map.put(17, vo.getHtmlContent());
        if (vo.getJsonContent() != null) {
            map.put(18, vo.getJsonContent());
        } else {
            map.put(18, "");
        }

        return map;

    }

}
