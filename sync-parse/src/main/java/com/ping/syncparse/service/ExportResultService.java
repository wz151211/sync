package com.ping.syncparse.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import com.ping.syncparse.utils.ExcelUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class ExportResultService {

    @Autowired
    private CaseXsMapper caseMapper;
    private int pageSize = 5000;
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
            File file = new File("E:\\导出\\刑事案件-12.xlsx");
            if (file.exists()) {
                file.delete();
            } else {
                file.createNewFile();
            }
            out = new FileOutputStream(file);
            ExcelUtils.export(wb, sheet, list, head);
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
            crimeHead2.add("判决结果");
            crimeHead2.add("序号");
            crimeHead2.add("案号");
            crimeHead2.add("姓名");
            crimeHead2.add("罪名");
            crimeHead2.add("刑期");
            List<String> t = new ArrayList<>();
            t.add("姓名");
            t.add("罪名");
            t.add("刑期");
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
            List<Map<Integer, Object>> partyList = new ArrayList<>();
            List<Map<Integer, Object>> partyList2 = new ArrayList<>();
            List<Map<Integer, Object>> crimeList = new ArrayList<>();
            List<Map<Integer, Object>> crimeList2 = new ArrayList<>();

            for (CaseVo vo : vos) {
                List<PartyEntity> party = vo.getParty();
                if (party != null) {
                    Map<String, List<PartyEntity>> listMap = party.stream().filter(c -> org.springframework.util.StringUtils.hasLength(c.getType())).collect(Collectors.groupingBy(PartyEntity::getType));
                    Map<Integer, Object> partyMap = new HashMap<>();
                    partyMap.put(1, vo.getCaseNo());
                    partyList.add(partyMap);
                    int start = 0;
                    int count = 0;
                    List<PartyEntity> entities = listMap.get("原告");
                    if (entities != null && entities.size() > 0) {
                        for (int i = 0; i < entities.size(); i++) {
                            PartyEntity entity = entities.get(i);
                        /*    entity.setAge("");
                            try {
                                if (StringUtils.isNotEmpty(entity.getAgeContent())) {
                                    entity.setAge(DateUtil.age(DateUtil.parse(entity.getAgeContent()), vo.getRefereeDate()) + "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
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
                     /*       entity.setAge("");
                            try {
                                if (StringUtils.isNotEmpty(entity.getAgeContent())) {
                                    entity.setAge(DateUtil.age(DateUtil.parse(entity.getAgeContent()), vo.getRefereeDate()) + "");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }*/
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
                        Map<Integer, Object> crimeMap2 = new HashMap<>();
                        CrimeVO crimeVO = crimes.get(i);
                        toCrime(start, crimeMap, crimeVO);
                        crimeMap2.put(1, vo.getCaseNo());
                        toCrime(crimeMap2, crimeVO);
                        crimeList2.add(crimeMap2);
                        start += 3;
                    }
                } else {
                    crimeList.add(new HashMap<>());
                }
            }
    /*        Sheet partySheet = wb.createSheet("当事人信息");
            ExcelUtils.export(wb, partySheet, partyList, partyHead.toArray());*/

            Sheet partySheet2 = wb.createSheet("当事人信息");
            ExcelUtils.export(wb, partySheet2, partyList2, partyHead2.toArray());

         //   Sheet crimeSheet = wb.createSheet("判决结果");
         //   ExcelUtils.export(wb, crimeSheet, crimeList2, crimeHead2.toArray());
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

    public void htmlAsAltChunk2Docx(String html, String docxPath) {

        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            //   wordMLPackage.setFontMapper(IFontHandler.getFontMapper());
            // Add the Html altChunk
            //  String html = sb.toString();
            if (StringUtils.isEmpty(html)) {
                mdp.addAltChunk(AltChunkType.Html, "<html><center>不公开理由：人民法院认为不宜在互联网公布的其他情形</center></html>".getBytes(StandardCharsets.UTF_8));

            } else {
                mdp.addAltChunk(AltChunkType.Html, html.getBytes(StandardCharsets.UTF_8));

            }

            // Round trip
            WordprocessingMLPackage pkgOut = mdp.convertAltChunks();

            pkgOut.save(new File(docxPath));
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

    private Map<Integer, Object> toCrime(Map<Integer, Object> map, CrimeVO vo) {
        map.put(2, vo.getName());
        map.put(3, vo.getCrime());
        map.put(4, vo.getImprisonmentTerm());
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
