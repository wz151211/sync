package com.ping.syncparse.service.security;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(value = "document_zhengquan")
public class SecurityVo {
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
    private String text;


    private String judgmentResult;

    private String courtConsidered;
    private String litigationRecords;
    private JSONArray legalBasis;
    private String fact;
    private JSONObject jsonContent;

    private String province;

    private String city;

    private String county;

    private String uid;

}
