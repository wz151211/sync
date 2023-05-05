package com.ping.syncparse.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * @author: W.Z
 * @date: 2019/10/15 16:22
 * @desc: excel导出工具类
 */
public class ExcelUtils {

    public static void setHeader(HttpServletRequest request, HttpServletResponse response, String fileName) {
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        String agent = request.getHeader("USER-AGENT").toLowerCase();
        try {
            if (agent.contains("firefox")) {
                response.setCharacterEncoding("utf-8");
                response.setHeader("content-disposition", "attachment;filename=" + new String(fileName.getBytes(), "ISO8859-1") + ".xlsx");
            } else {
                response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8") + ".xlsx");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static void export(Workbook wb, Sheet sheet, List<Map<Integer, Object>> data, Object[] header) {
        try {
            createHeader(sheet, header);
            fillData(sheet, data, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createHeader(Sheet sheet, Object[] header) {
        Row row = null;
        row = sheet.createRow(0);
        row.setHeightInPoints(30);
        Workbook wb = sheet.getWorkbook();
        // 合并单元格
        CellRangeAddress range = new CellRangeAddress(0, 0, 0, header.length - 2);
        sheet.addMergedRegion(range);
        RegionUtil.setBorderBottom(BorderStyle.THIN, range, sheet);
        RegionUtil.setBorderTop(BorderStyle.THIN, range, sheet);
        RegionUtil.setBorderLeft(BorderStyle.THIN, range, sheet);
        RegionUtil.setBorderRight(BorderStyle.THIN, range, sheet);
        Cell cell = row.createCell(0);
        cell.setCellValue(header[0].toString());
        cell.setCellStyle(getCellStyle(wb, (short) 24));
        row = sheet.createRow(1);
        CellStyle style = getCellStyle(wb, (short) 12);
        for (int i = 1; i < header.length; i++) {
            cell = row.createCell(i - 1);
            cell.setCellValue(header[i].toString());
            cell.setCellStyle(style);
            // 设置列宽
            sheet.setColumnWidth(i - 1, 15 * 256);
        }
    }

    public static void fillData(Sheet sheet, List<Map<Integer, Object>> data, boolean first) {
        Row row = null;
        Cell cell = null;
        Workbook wb = sheet.getWorkbook();
        CellStyle style = getCellStyle(wb, (short) 11);
        int lastRowNum = 0;
        Sheet sheet1 = null;
        if (wb instanceof SXSSFWorkbook) {
            SXSSFWorkbook workbook = (SXSSFWorkbook) wb;
            XSSFWorkbook workbook1 = workbook.getXSSFWorkbook();
            sheet1 = workbook1.getSheet(sheet.getSheetName());
            lastRowNum = sheet1.getLastRowNum();
        } else {
            lastRowNum = sheet.getLastRowNum();
            sheet1 = sheet;
        }

        System.out.println("=========" + sheet1.getSheetName() + "===========");
        System.out.println("=========" + lastRowNum + "===========");
        for (int i = 0; i < data.size(); i++) {
            Map<Integer, Object> map = data.get(i);
            if (first) {
                row = sheet1.createRow(i + 2);
            } else {
                row = sheet1.createRow(lastRowNum + i + 1);
            }

            row.setHeight((short) 1000);
            // 序号
            cell = row.createCell(0);
            if (lastRowNum < 0) {
                cell.setCellValue(String.valueOf(i + 1));
            } else {
                cell.setCellValue(String.valueOf(lastRowNum + i - 1));
            }


            cell.setCellStyle(style);
            for (int j = 1; j <= map.size(); j++) {
                cell = row.createCell(j);
                try {
                    cell.setCellValue(map.get(j) == null ? "" : map.get(j).toString());
                } catch (Exception e) {
                    //   e.printStackTrace();
                }
                cell.setCellStyle(style);
            }

        }
    }

    private static CellStyle getCellStyle(Workbook wb, short fontHeightInPoints) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setFontName("宋体");
        // 设置字体大小
        style.setFont(font);
        font.setFontHeightInPoints(fontHeightInPoints);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        // 设置自动换行
        style.setWrapText(true);
        return style;
    }

}
