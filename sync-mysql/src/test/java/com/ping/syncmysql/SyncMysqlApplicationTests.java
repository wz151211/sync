package com.ping.syncmysql;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncmysql.hander.*;
import com.ping.syncmysql.task.local.DocumentEntity;
import com.ping.syncmysql.task.local.DocumentMapper;
import com.ping.syncmysql.task.remote.RemoteTempDocumentMapper;
import com.ping.syncmysql.task.remote.RemotrDocumentEntity;
import com.ping.syncmysql.temp.ProtectionEntity;
import com.ping.syncmysql.temp.ProtectionMapper;
import com.ping.syncmysql.temp.ProtectionService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = SyncMysqlApplication.class)
@RunWith(SpringRunner.class)
class SyncMysqlApplicationTests {

    @Autowired
    private ParseService parseService;

    @Test
    void contextLoads() {
    }

    @Test
    public void test1() throws Exception {
        File file = new File("E:\\文化");
        ParseEntity entity = null;
        if (file.isDirectory()) {
            for (File listFile : file.listFiles()) {
                for (File file1 : listFile.listFiles()) {
                    entity = new ParseEntity();
                    entity.setContent(new String(Files.readAllBytes(file1.toPath())));
                    List<String> lines = Files.readAllLines(file1.toPath());
                    entity.setName(lines.get(0));
                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
                        if (!StringUtils.hasLength(entity.getCaseNo()) && i < 6) {
                            if (line.contains("号") && line.length() < 50) {
                                int index = line.indexOf("书");
                                if (index > 0) {
                                    entity.setCaseNo(line.substring(index + 1));
                                } else {
                                    int index1 = line.indexOf("法院");
                                    if (index1 > 0) {
                                        entity.setCaseNo(line.substring(index1 + 2));
                                        entity.setCourtName(line.substring(0, index + 2));
                                    } else {
                                        entity.setCaseNo(line);
                                    }

                                }
                            }
                        }
                        if (!StringUtils.hasLength(entity.getCourtName()) && i < 5) {
                            if (line.contains("法院") && line.length() < 20) {
                                entity.setCourtName(line);
                            }
                        }
                        if (line.length() < 12) {
                            if (line.contains("年") && line.contains("月") && line.contains("日")) {
                                String date = line.replace("〇", "0");
                                date = date.replace("一", "1");
                                date = date.replace("二", "2");
                                date = date.replace("三", "3");
                                date = date.replace("四", "4");
                                date = date.replace("五", "5");
                                date = date.replace("六", "6");
                                date = date.replace("七", "7");
                                date = date.replace("八", "8");
                                date = date.replace("九", "9");
                                date = date.replace("十日", "10");
                                date = date.replace("十月", "10月");
                                date = date.replace("十", "");
                                date = date.replace("年", "-");
                                date = date.replace("月", "-");
                                date = date.replace("日", "");
                                date = date.replace("０", "0");
                                date = date.replace("○", "0");
                                date = date.replace("Ｏ", "0");
                                date = date.replace("o", "0");
                                date = date.replace("ｏ", "0");
                                date = date.replace("××", "01");
                                date = date.replace("元", "01");
                                date = date.replace(" ", "");
                                try {
                                    System.out.println("line=" + line);
                                    String[] split = date.split("-");
                                    if (split[1].length() == 1) {
                                        split[1] = "0" + split[1];
                                    }
                                    if (split[2].length() == 1) {
                                        split[2] = "0" + split[2];
                                    }
                                    if (split[2].length() == 3) {
                                        split[2] = split[2].replace("1", "");
                                    }
                                    date = split[0].trim() + "-" + split[1].trim() + "-" + split[2].trim();
                                    System.out.println("date=" + date);
                                    //  DateTime dateTime = DateUtil.parse(date.trim());
                                    Date parse = org.assertj.core.util.DateUtil.parse(date.trim());
                                    entity.setRefereeDate(parse);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }
                    parseService.save(entity);
                }

            }

        }
    }

    @Test
    public void testUpdate() throws FileNotFoundException {
        ParseEntity parseEntity = new ParseEntity();
        //  File file = new File("E:\\excel\\【北大法宝】目录列表.xls");
        //   File file = new File("E:\\excel\\【北大法宝】目录列表 (14).xls");
        //   File file = new File("E:\\excel\\【北大法宝】目录列表 (21).xls");
        //    File file = new File("E:\\excel\\【北大法宝】目录列表 (22).xls");

        //   File file = new File("E:\\excel\\【北大法宝】目录列表 (23).xls");


        File file = new File("E:\\excel");
        List<File> list = new ArrayList<>();
        for (File listFile : file.listFiles()) {
            if (listFile.getName().contains("zip")) {
                continue;
            }
            if (listFile.isDirectory()) {
                for (File file1 : listFile.listFiles()) {
                    list.add(file1);
                }

            } else {
                list.add(listFile);
            }
        }
        for (File listFile : list) {
            FileInputStream fileInputStream = new FileInputStream(listFile);
            try (Workbook wb = new HSSFWorkbook(fileInputStream)) {
                Sheet sheet = wb.getSheetAt(0);
                int lastRowNum = sheet.getLastRowNum();
                for (int i = 4; i < lastRowNum; i++) {
                    Row row = sheet.getRow(i);
                    ParseEntity entity = new ParseEntity();
                    String name = row.getCell(1).getStringCellValue();
                    String trialProceedings = row.getCell(2).getStringCellValue();
                    String cause = row.getCell(3).getStringCellValue();
                    String docType = row.getCell(4).getStringCellValue();
                    String courtName = row.getCell(5).getStringCellValue();
                    String casseNo = row.getCell(6).getStringCellValue();
                    String refereeDate = row.getCell(7).getStringCellValue();
                    if (StringUtils.hasLength(refereeDate)) {
                        refereeDate = refereeDate.replace(".", "-");
                    }
                    entity.setName(name);
                    entity.setTrialProceedings(trialProceedings);
                    entity.setCause(cause);
                    entity.setDocType(docType);
                    entity.setCourtName(courtName);
                    entity.setCaseNo(casseNo);
                    try {
                        entity.setRefereeDate(DateUtil.parse(refereeDate));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    parseService.update(entity);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            parseService.update(parseEntity);
        }

    }

    @Autowired
    private RemoteTempDocumentMapper remoteDocumentMapper;
    @Autowired
    private DocumentMapper documentMapper;

    @Test
    public void sync() {
        List<DocumentEntity> entities = documentMapper.selectList(Wrappers.<DocumentEntity>lambdaQuery().like(DocumentEntity::getHtmlContent, "文化大革命"));
        for (DocumentEntity entity : entities) {
            RemotrDocumentEntity remotrDocumentEntity = new RemotrDocumentEntity();
            BeanUtils.copyProperties(entity, remotrDocumentEntity);
            remotrDocumentEntity.setUid(remotrDocumentEntity.getCaseNo() + remotrDocumentEntity.getRefereeDate());
            try {
                remoteDocumentMapper.insert(remotrDocumentEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private ParseMapper parseMapper;
    @Autowired
    private ParseAllMapper parseAllMapper;

    @Test
    public void sync1() {
        //  List<DocumentEntity> entities = documentMapper.selectList(Wrappers.<DocumentEntity>lambdaQuery().like(DocumentEntity::getHtmlContent, "文化大革命"));
        List<ParseEntity> entities = parseMapper.selectList(Wrappers.lambdaQuery());
        for (ParseEntity entity : entities) {
            ParseAllEntity allEntity = new ParseAllEntity();
            BeanUtils.copyProperties(entity, allEntity);
            String uid = allEntity.getCaseNo() + allEntity.getRefereeDate();
            //  allEntity.setUid(MD5.create().digestHex16(uid));
            allEntity.setUid(uid);
            try {
                parseAllMapper.insert(allEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void sync2() {
        List<RemotrDocumentEntity> entities = remoteDocumentMapper.selectList(Wrappers.<RemotrDocumentEntity>lambdaQuery());
        for (RemotrDocumentEntity entity : entities) {
            ParseAllEntity allEntity = new ParseAllEntity();
            BeanUtils.copyProperties(entity, allEntity);
            String uid = allEntity.getCaseNo() + allEntity.getRefereeDate();

            Document document = Jsoup.parse(entity.getHtmlContent());
            Elements elements = document.body().children();
            StringBuilder sb = new StringBuilder();
            sb.append(allEntity.getName()).append("\r\n");
            for (Element element : elements) {
                sb.append(element.text()).append("\r\n");
            }
            // text = allEntity.getName() + "\r\n" + text;
            allEntity.setContent(sb.toString());
            //allEntity.setUid(MD5.create().digestHex16(uid));
            try {
                allEntity.setRefereeDate(DateUtil.parse(entity.getRefereeDate()).toJdkDate());
            } catch (Exception e) {
                e.printStackTrace();
            }
            allEntity.setUid(entity.getUid());
            try {
                parseAllMapper.insert(allEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private ProtectionMapper protectionMapper;

    @Test
    public void test4() {
        try {
            File file = new File("E:\\行政处罚-环保-00.xlsx");
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fileInputStream);
            Sheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i < lastRowNum; i++) {
                ProtectionEntity entity = new ProtectionEntity();
                Row row = sheet.getRow(i);
                entity.setGid(row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null);
                entity.setTitle(row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null);
                entity.setDocumentNO(row.getCell(3) != null ? row.getCell(3).getStringCellValue() : null);
                entity.setPunishmentDate(row.getCell(4) != null ? row.getCell(4).getStringCellValue() : null);
                String s = row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null;
                if (StringUtils.hasLength(s)) {
                    try {
                        System.out.println(s);
                        JSONArray objects = JSON.parseArray(s);
                        entity.setPunishmentObject(objects.stream().map(Object::toString).collect(Collectors.joining(",")));
                    } catch (Exception e) {
                        e.printStackTrace();
                        entity.setPunishmentObject(s);
                    }
                }

                entity.setEnforcementLevel(row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null);
                entity.setLawRegional(row.getCell(7) != null ? row.getCell(7).getStringCellValue() : null);
                entity.setLawDepartment(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : null);
                entity.setPunishmentTarget(row.getCell(9) != null ? row.getCell(9).getStringCellValue() : null);
                entity.setPunishmentType(row.getCell(10) != null ? row.getCell(10).getStringCellValue() : null);
                entity.setAccordingLaw(row.getCell(11) != null ? row.getCell(11).getStringCellValue() : null);
                entity.setCategory(row.getCell(12) != null ? row.getCell(12).getStringCellValue() : null);
                entity.setAllText(row.getCell(13) != null ? row.getCell(13).getStringCellValue() : null);
                try {
                    protectionMapper.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private ProtectionService protectionService;

    @Test
    public void test5() {
        protectionService.update();
    }

    @Test
    public void test6() {
        protectionService.updateRelated();
    }


}
