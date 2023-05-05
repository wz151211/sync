package com.ping.syncparse.service;

import lombok.Data;

@Data
public class CaseSummaryVO {


    private String caseNo;
    private String retrialCaseNo;

    private String registerCaseDate;

    private String judgmentDate;

    private String disputedAmount;

    //违约金
    private String iquidatedDamages;

    //罚息
    private String defaultInterest;

    private String finalInstance;

    private String change = "否";

    private String plaintiff;

    private String defendant;

    private String province;
    private String city;
    private String county;

    private String hearingFees;

    private String primary;

    private String summary = "否";

    private String small = "否";
}
