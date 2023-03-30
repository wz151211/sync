package com.ping.syncmysql.temp;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class ProtectionService {
    @Autowired
    private ProtectionMapper protectionMapper;

    public void update() {
        try {
            File file = new File("E:\\1上市公司名单（含全称）.xlsx");
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fileInputStream);
            Sheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i < lastRowNum; i++) {
                Row row = sheet.getRow(i);
                String symbol = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null;
                String shortName = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
                String fullName = row.getCell(2) != null ? row.getCell(2).getStringCellValue() : null;
                for (ProtectionEntity entity : protectionMapper.get(fullName)) {
                    ProtectionEntity e = new ProtectionEntity();
                    e.setGid(entity.getGid());
                    e.setSymbol(symbol);
                    e.setShortName(shortName);
                    e.setFullName(fullName);
                    protectionMapper.update(e);
                }
 /*               for (ProtectionEntity entity : protectionMapper.get(shortName)) {
                    ProtectionEntity e = new ProtectionEntity();
                    e.setGid(entity.getGid());
                    e.setSymbol(symbol);
                    e.setShortName(shortName);
                    e.setFullName(fullName);
                    protectionMapper.update(e);
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateRelated() {
        try {
            File file = new File("E:\\2关联企业名称.xlsx");
            FileInputStream fileInputStream = new FileInputStream(file);
            Workbook wb = new XSSFWorkbook(fileInputStream);
            Sheet sheet = wb.getSheetAt(0);
            int lastRowNum = sheet.getLastRowNum();
            for (int i = 1; i < lastRowNum; i++) {
                Row row = sheet.getRow(i);
                String symbol = row.getCell(0) != null ? row.getCell(0).getStringCellValue() : null;
                String relatedParty = row.getCell(1) != null ? row.getCell(1).getStringCellValue() : null;
                for (ProtectionEntity entity : protectionMapper.get(relatedParty)) {
                    ProtectionEntity e = new ProtectionEntity();
                    e.setGid(entity.getGid());
                    e.setSymbol(symbol);
                    e.setRelatedParty(relatedParty);
                    protectionMapper.update(e);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importDate1() {
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
                try {
                    System.out.println(entity.getPunishmentObject());

                    protectionMapper.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importDate2() {
        try {
            File file = new File("E:\\行政处罚-环保-01.xlsx");
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
                try {
                    System.out.println(entity.getPunishmentObject());

                    protectionMapper.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void importDate3() {
        try {
            File file = new File("E:\\行政处罚-环保-02.xlsx");
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
                try {
                    System.out.println(entity.getPunishmentObject());
                    protectionMapper.insert(entity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
