package com.ping.syncparse.service.contract;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.PartyEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(value = "document_contract_copy1")
public class ContractVo {
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
