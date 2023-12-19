package com.ping.syncparse.service.criminal;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2023/12/19 21:42
 */
@Data
@Document(value = "document_sharen")
public class CriminalVO {
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

}
