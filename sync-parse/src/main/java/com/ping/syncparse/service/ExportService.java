package com.ping.syncparse.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import com.ping.syncparse.utils.ExcelUtils;
import org.apache.commons.io.FilenameUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Service
public class ExportService {

    @Autowired
    private CaseXsMapper caseMapper;
    private int pageSize = 30000;
    private AtomicInteger pageNum = new AtomicInteger(-1);

    @Autowired
    private DocumentXsMapper documentXsMapper;
    public void export() {
        pageNum.getAndIncrement();
        List<DocumentXsLhEntity> vos = documentXsMapper.findList(pageNum.get(), pageSize, null);
 /*       Workbook wb = new XSSFWorkbook();
        String[] head = {"案件信息", "序号", "案件名称", "案号", "法院名称", "裁判日期", "案由", "案件类型", "审判程序", "文书类型", "省份", "地市", "区县",
                "事实/审理查明", "判决结果", "理由", "法律依据", "诉讼记录", "HTML内容", "JSON内容"};
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
        }*/
        String target = "G:\\虚开增值税专用发票、用于骗取出口退税、抵扣税款发票\\";

        for (DocumentXsLhEntity vo : vos) {
            String name = vo.getName();
            name = name.replace("span>","");
            vo.setName(name);
            String docPath = target + FilenameUtils.getBaseName(vo.getName()) + ".docx";
            File docFile = new File(docPath);
            if (docFile.exists()) {
                docPath = target + FilenameUtils.getBaseName(vo.getName()) + "-" + RandomUtil.randomString(5) + ".docx";
            }
            htmlAsAltChunk2Docx(vo.getHtmlContent(), docPath);
        }
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

    private Map<Integer, Object> toMap(DocumentXsLhEntity vo) {
        String[] head = {"案件信息", "序号", "案件名称", "案号", "法院名称", "裁判日期", "案由", "案件类型", "审判程序", "文书类型", "省份", "地市", "区县",
                "事实/审理查明", "判决结果", "理由", "法律依据", "诉讼记录", "HTML内容", "JSON内容"};
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
        if (vo.getCause() != null && vo.getCause().size() > 0) {
            map.put(5, vo.getCause().stream().map(Object::toString).collect(joining(",")));
        }
        if (vo.getLegalBasis() != null && vo.getLegalBasis().size() > 0) {
            for (int i = 0; i < vo.getLegalBasis().size(); i++) {
            }
            map.put(15, vo.getLegalBasis().stream().map(c -> {
                JSONObject aa = JSONObject.parseObject(JSON.toJSONString(c));
                return aa.getString("fgmc") + aa.getString("tkx");
            }).collect(joining(",")));
        }
        map.put(6, vo.getCaseType());
        map.put(7, vo.getTrialProceedings());
        map.put(8, vo.getDocType());
        map.put(9, vo.getProvince());
        map.put(10, vo.getCity());
        map.put(11, vo.getCounty());
        map.put(12, vo.getFact());
        map.put(13, vo.getJudgmentResult());
        map.put(14, vo.getCourtConsidered());
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
