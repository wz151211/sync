package com.ping.syncmysqltomongo.mongo.temp;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(value = "doc_minjinajiedai_data_1")
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


    //借款金额
    private String loanAmount1;
    private String loanAmountContent1;

    private String loanAmount2;
    private String loanAmountContent2;
    //借款开始时间
    private String contractStartDate1;
    private String contractStartDateContent1;

    private String contractStartDate2;
    private String contractStartDateContent2;
    //借款结束时间
    private String contractEndDate1;
    private String contractEndDateContent1;

    private String contractEndDate2;
    private String contractEndDateContent2;

    //借款利率
    private String loanRate1;
    private String loanRateContent1;

    private String loanRate2;
    private String loanRateContent2;

    //借款利率
    private String rateType1;

    private String rateType2;

    private String rateTerm1;

    private String rateTerm2;

    //期限
    private Integer term;

    private String termContent;


    private String judgeContent;

    private int words;

    private String registerCaseDate;
    private String registerCaseDateContent;

    private String judgmentDescContent;

    private String hearingFees;
}
