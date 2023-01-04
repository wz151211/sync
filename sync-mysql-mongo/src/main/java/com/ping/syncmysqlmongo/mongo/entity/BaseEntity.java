package com.ping.syncmysqlmongo.mongo.entity;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

@Data
public class BaseEntity {
    @Id
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
