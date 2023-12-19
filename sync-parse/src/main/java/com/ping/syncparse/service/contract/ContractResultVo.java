package com.ping.syncparse.service.contract;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.PartyEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Document(value = "document_contract_data_no")
public class ContractResultVo {
    @Id
    @ExcelIgnore
    private String id;

    @ExcelProperty(value = "案件名称", index = 0)
    private String name;

    @ExcelProperty(value = "案号", index = 1)
    private String caseNo;

    @ExcelProperty(value = "法院名称", index = 2)
    private String courtName;

    @ExcelProperty(value = "裁判日期", index = 3)
    @DateTimeFormat("yyyy-MM-dd")
    private Date refereeDate;

    @ExcelProperty(value = "案由", index = 4)
    private String cause;

    private String keyword;

    @ExcelProperty(value = "案件类型", index = 5)
    private String caseType;

    @ExcelProperty(value = "文书类型", index = 6)
    private String docType;

    @ExcelProperty(value = "审批程序", index = 7)
    private String trialProceedings;

    @ExcelProperty(value = "省份", index = 8)
    private String province;

    @ExcelProperty(value = "地市", index = 9)
    private String city;

    @ExcelProperty(value = "区县", index = 10)
    private String county;

    @ExcelIgnore
    private List<PartyEntity> party = new ArrayList<>();

    @ExcelProperty(value = "判决结果", index = 11)
    private String judgmentResult;

    @ExcelProperty(value = "法院认为", index = 12)
    private String courtConsidered;

    @ExcelProperty(value = "诉讼记录", index = 13)
    private String litigationRecords;

    @ExcelProperty(value = "法律依据", index = 14)
    private String legalBasis;

    @ExcelProperty(value = "事实", index = 15)
    private String fact;

    //@ExcelProperty(value = "HTML内容", index = 16)
    @ExcelIgnore
    private String htmlContent;

    @ExcelIgnore
    private JSONObject jsonContent;

    //合同名称
    private String contractName;
    private String contractNameContent;
    //
    private String contractSigningDate;
    private String contractSigningDateContent;
    //借款金额
    private String loanAmount;
    private String loanAmountContent;
    //借款开始时间
    private String contractStartDate;
    private String contractStartDateContent;
    //借款结束时间
    private String contractEndDate;
    private String contractEndDateContent;

    //借款利率
    private String loanRate;
    private String loanRateContent;

    //借款利率
    private String rateType;

    //逾期利率
    private String overdueRate;
    private String overdueRateContent;

    //抵押条件
    private String mortgage;
    private String mortgageContent;

    //违约日期
    private String defaultDate;
    private String defaultDateContent;

    //违约金额
    private String defaultAmount;
    private String defaultAmountContent;


    private String judgmentDesc;
    //诉讼费
    private String hearingFees;
    //被告受理费
    private String defendantHearingFees;
    //原告受理费
    private String plaintiffHearingFees;

    //期限
    private Integer term;

    private String termContent;


}
