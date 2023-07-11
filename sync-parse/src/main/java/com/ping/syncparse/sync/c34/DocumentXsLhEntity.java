package com.ping.syncparse.sync.c34;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@Document(value = "document_quanshujiufen")
public class DocumentXsLhEntity {

    @Id
    private String id;

    private String tId;

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

    private String courtConsidered;

    private String judgmentResult;

    private String litigationRecords;
    private JSONArray legalBasis;
    private String fact;

    private String province;

    private String city;

    private String county;
}
