package com.ping.syncparse.service.contract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document(value = "document_jiekuanjiufen_copy1")
public class ContractVo {
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
