package com.ping.syncparse.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2022/12/15 22:26
 */
@Data
@ContentFontStyle(fontHeightInPoints = 14)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@ColumnWidth(25)
@HeadRowHeight(25)
@HeadStyle
@HeadFontStyle(fontHeightInPoints = 12)
@Document(value = "party")
public class PartyEntity {

    @Id
    private String id;

    private String caseId;

    @ExcelProperty(value = "案号", index = 0)
    private String caseNo;

    @ExcelProperty(value = "类型", index = 1)
    private String type;

    @ExcelProperty(value = "案件名称", index = 2)
    private String name;

    @ExcelProperty(value = "性别", index = 3)
    private String sex;

    @ExcelProperty(value = "年龄", index = 4)
    private String age;

    private String ageContent;

    @ExcelProperty(value = "出生日期", index = 5)
    private String birthday;

    @ExcelProperty(value = "民族", index = 6)
    private String nation;

    @ExcelProperty(value = "省份", index = 7)
    private String province;

    @ExcelProperty(value = "地市", index = 8)
    private String city;

    @ExcelProperty(value = "区县", index = 9)
    private String county;

    @ExcelProperty(value = "地址", index = 10)
    private String address;

    @ExcelProperty(value = "学历", index = 11)
    private String eduLevel;

    @ExcelProperty(value = "职业", index = 12)
    private String profession;

    @ExcelProperty(value = "案件名称", index = 13)
    private String content;

}
