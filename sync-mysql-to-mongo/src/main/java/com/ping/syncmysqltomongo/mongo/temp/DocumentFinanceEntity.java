package com.ping.syncmysqltomongo.mongo.temp;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(value = "document_xinxingzhuanli_result")
public class DocumentFinanceEntity {
    @Id
    private String id;

    private String name;

    private String caseNo;

    private String courtName;

    private Date refereeDate;

    private String cause;

    private String keyword;

    private String caseType;

    private String docType;

    private String trialProceedings;

    private String province;

    private String city;

    private String county;

    private List<PartyEntity> party = new ArrayList<>();

    private String judgmentResult;

    private String courtConsidered;

    private String litigationRecords;

    private String legalBasis;
    private String legalBasisCount;

    private String fact;

    private String htmlContent;

    private JSONObject jsonContent;

    //诉讼请求
    private String litigationClaims;
    //判决结果
    private String judgmentDesc;
}
