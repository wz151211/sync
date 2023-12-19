package com.ping.syncmysqltomongo.mysql.temp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
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
@TableName(value = "document_tudi_nongcuntudi")
public class TempDocumentEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField("name")
    private String name;

    @TableField("case_no")
    private String caseNo;

    @TableField("court_name")
    private String courtName;

    @TableField("referee_date")
    private Date refereeDate;


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

    @TableField("judgment_result")
    private String judgmentResult;

    @TableField("court_considered")
    private String courtConsidered;

    @TableField("litigation_records")
    private String litigationRecords;

    @TableField("legal_basis")
    private String legalBasis;

    @TableField("fact")
    private String fact;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("county")
    private String county;
}
