package com.ping.syncmysql.hander;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName(value = "cpws_wh")
public class ParseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("name")
    private String name;

    @TableField("case_no")
    private String caseNo;

    @TableField("court_name")
    private String courtName;

    @TableField("referee_date")
    private Date refereeDate;

    @TableField("content")
    private String content;

    @TableField("case_type")
    private String caseType;

    @TableField("cause")
    private String cause;

    @TableField("trial_proceedings")
    private String trialProceedings;

    @TableField("doc_type")
    private String docType;
}
