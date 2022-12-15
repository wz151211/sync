package com.ping.syncmysqltomongo.mongo;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@Document(value = "document")
public class DocumentEntity {


    @MongoId
    private String id;


    private String name;


    private String caseNo;


    private String courtName;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date refereeDate;


    private String caseType;


    private String[] cause;


    private String[] party;

    private String[] keyword;


    private String trialProceedings;


    private String docType;


    private String htmlContent;


    private JSONObject jsonContent;


    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date createTime;
}
