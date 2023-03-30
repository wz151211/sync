package com.ping.syncmysql.temp;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pkulaw_punish")
public class ProtectionEntity {
    @TableId("gid")
    @ExcelProperty("Gid")
    private String gid;

    @TableField("title")
    @ExcelProperty("Title")
    private String title;

    @TableField("document_no")
    @ExcelProperty("DocumentNO")
    private String DocumentNO;

    @TableField("punishment_date")
    @ExcelProperty("PunishmentDate")
    private String PunishmentDate;

    @TableField("punishment_object")
    @ExcelProperty("PunishmentObject")
    private String PunishmentObject;

    @TableField("enforcement_level")
    @ExcelProperty("EnforcementLevel")
    private String EnforcementLevel;

    @TableField("law_regional")
    @ExcelProperty("LawRegional")
    private String LawRegional;

    @TableField("law_department")
    @ExcelProperty("LawDepartment")
    private String LawDepartment;

    @TableField("punishment_target")
    @ExcelProperty("PunishmentTarget")
    private String PunishmentTarget;

    @TableField("punishment_type")
    @ExcelProperty("PunishmentType")
    private String PunishmentType;

    @TableField("according_law")
    @ExcelProperty("AccordingLaw")
    private String AccordingLaw;

    @TableField("category")
    @ExcelProperty("Category")
    private String Category;

    @TableField("text")
    @ExcelProperty("AllText")
    private String AllText;

    @TableField("symbol")
    @ExcelProperty("symbol")
    private String symbol;

    @TableField("short_name")
    @ExcelProperty("ShortName")
    private String shortName;

    @TableField("full_name")
    @ExcelProperty("FullName")
    private String fullName;

    @TableField("related_party")
    @ExcelProperty("RelatedParty")
    private String relatedParty;
}
