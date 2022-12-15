package com.ping.syncmysqlmongo.mongo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
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


    private Date refereeDate;


    private String caseType;


    private JSONArray cause;


    private JSONArray party;

    private JSONArray keyword;


    private String trialProceedings;


    private String docType;


    private String htmlContent;


    private JSONObject jsonContent;

    private Date createTime;
}
