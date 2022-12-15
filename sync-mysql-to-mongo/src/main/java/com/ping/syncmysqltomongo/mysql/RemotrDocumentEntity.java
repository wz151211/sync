package com.ping.syncmysqltomongo.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@TableName(value = "document")
public class RemotrDocumentEntity {

    @TableId(type = IdType.INPUT)
    @MongoId
    private String id;

    @TableField("name")
    private String name;

    @TableField("case_no")
    private String caseNo;

    @TableField("court_name")
    private String courtName;

    @TableField("referee_date")
    private String refereeDate;


    @TableField("case_type")
    private String caseType;

    @TableField("cause")
    private String cause;

    @TableField("party")
    private String party;

    @TableField("keyword")
    private String keyword;

    @TableField("trial_proceedings")
    private String trialProceedings;

    @TableField("doc_type")
    private String docType;

    @TableField("html_content")
    private String htmlContent;

    @TableField("json_content")
    private String jsonContent;

    @TableField("create_time")
    private Date createTime;
}
