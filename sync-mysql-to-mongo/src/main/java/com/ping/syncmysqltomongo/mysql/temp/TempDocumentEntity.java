package com.ping.syncmysqltomongo.mysql.temp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@TableName(value = "document1")
public class TempDocumentEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField("name")
    private String name;

    @TableField("case_no")
    private String caseNo;

    @TableField("court_name")
    private String courtName;

    @TableField("referee_date")
    private Date refereeDate;


    @TableField("case_type")
    private String caseType;

    @TableField("cause")
    private String cause;

    @TableField("party")
    private String party;

    @TableField("keyword")
    private String keyword;

    @TableField("trial_proceedings")
    private String trialProceedings;

    @TableField("doc_type")
    private String docType;

  /*  @TableField("html_content")
    private String htmlContent;

    @TableField("json_content")
    private String jsonContent;*/

    @TableField("judgment_result")
    private String judgmentResult;

    @TableField("court_considered")
    private String courtConsidered;

    @TableField("litigation_records")
    private String litigationRecords;

    @TableField("legal_basis")
    private String legalBasis;

    @TableField("fact")
    private String fact;

    @TableField("province")
    private String province;

    @TableField("city")
    private String city;

    @TableField("county")
    private String county;
    @TableField("loan_amount1")
    private String loanAmount1;
    @TableField("loan_amount_content1")
    private String loanAmountContent1;

    @TableField("loan_amount2")
    private String loanAmount2;

    @TableField("loan_amount_content2")
    private String loanAmountContent2;
    //借款开始时间
    @TableField("contract_start_date1")
    private String contractStartDate1;

    @TableField("contract_start_date_content1")
    private String contractStartDateContent1;

    @TableField("contract_start_date2")
    private String contractStartDate2;
    @TableField("contract_start_date_content2")
    private String contractStartDateContent2;
    //借款结束时间
    @TableField("contract_end_date1")
    private String contractEndDate1;
    @TableField("contract_end_date_content1")
    private String contractEndDateContent1;

    @TableField("contract_end_date2")
    private String contractEndDate2;

    @TableField("contract_end_date_content2")
    private String contractEndDateContent2;

    //借款利率
    @TableField("loan_rate1")
    private String loanRate1;
    @TableField("loan_rate_content1")
    private String loanRateContent1;

    @TableField("loan_rate2")
    private String loanRate2;
    @TableField("loan_rate_content2")
    private String loanRateContent2;

    //借款利率
    @TableField("rate_type1")
    private String rateType1;
    @TableField("rate_type2")
    private String rateType2;

    @TableField("rate_term1")
    private String rateTerm1;

    @TableField("rate_term2")
    private String rateTerm2;

    //期限
    @TableField("term")
    private Integer term;
    @TableField("term_content")
    private String termContent;

   /* @TableField("judge_content")
    private String judgeContent;

    @TableField("words")
    private Integer words;

    @TableField("register_case_date")
    private String registerCaseDate;
    @TableField("register_case_date_content")
    private String registerCaseDateContent;

    @TableField("judgment_desc")
    private String judgmentDesc;
    @TableField("judgment_desc_content")
    private String judgmentDescContent;

    @TableField("hearing_fees")
    private String hearingFees;*/
}
