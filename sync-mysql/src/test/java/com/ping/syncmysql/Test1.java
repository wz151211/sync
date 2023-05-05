package com.ping.syncmysql;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ping.syncmysql.hander.ParseEntity;
import com.ping.syncmysql.temp.ProtectionEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class Test1 {
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
                    String s = lines.get(2);
                    int index = s.indexOf("书");
                    if (index > 0) {
                        entity.setCaseNo(s.substring(index + 1));
                    }

                    for (int i = 0; i < lines.size(); i++) {
                        String line = lines.get(i);
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
                                try {
                                    System.out.println("line=" + line);
                                    String[] split = date.split("-");
                                    if (split[1].length() == 1) {
                                        split[1] = 0 + split[1];
                                    }
                                    if (split[2].length() == 1) {
                                        split[2] = 0 + split[2];
                                    }
                                    date = split[0] + "-" + split[1] + "-" + split[2];
                                    System.out.println("date=" + date);
                                    DateTime dateTime = DateUtil.parse(date);
                                    entity.setRefereeDate(dateTime.toJdkDate());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                        }
                    }

                }

            }

        }
        System.out.println(entity);
    }

    @Test
    public void test2() {
        List<Object> list = new ArrayList<>();
        File file = new File("E:\\行政处罚-环保-00.xlsx");
        EasyExcel.read(file.getPath(), ProtectionEntity.class, new ReadListener() {
            @Override
            public void invoke(Object o, AnalysisContext analysisContext) {
                list.add(o);
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {

            }
        }).sheet().headRowNumber(1).doRead();
        for (Object o : list) {
            System.out.println(o);
        }

    }

    @Test
    public void test3() {
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
                entity.setPunishmentObject(row.getCell(5) != null ? row.getCell(5).getStringCellValue() : null);
                entity.setEnforcementLevel(row.getCell(6) != null ? row.getCell(6).getStringCellValue() : null);
                entity.setLawRegional(row.getCell(7) != null ? row.getCell(7).getStringCellValue() : null);
                entity.setLawDepartment(row.getCell(8) != null ? row.getCell(8).getStringCellValue() : null);
                entity.setPunishmentTarget(row.getCell(9) != null ? row.getCell(9).getStringCellValue() : null);
                entity.setPunishmentType(row.getCell(10) != null ? row.getCell(10).getStringCellValue() : null);
                entity.setAccordingLaw(row.getCell(11) != null ? row.getCell(11).getStringCellValue() : null);
                entity.setCategory(row.getCell(12) != null ? row.getCell(12).getStringCellValue() : null);
                entity.setAllText(row.getCell(13) != null ? row.getCell(13).getStringCellValue() : null);
                System.out.println(entity);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test6(){
        JSONArray objects = JSON.parseArray("['镇平县允鑫混凝土有限公司']");
        System.out.println(objects.get(0));
    }
}
