package com.ping.syncparse.service;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.*;
import com.alibaba.excel.enums.poi.HorizontalAlignmentEnum;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.PartyEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

/**
 * @Author: W.Z
 * @Date: 2022/12/31 15:21
 */
@Data
@ContentFontStyle(fontHeightInPoints = 14)
@ContentStyle(horizontalAlignment = HorizontalAlignmentEnum.CENTER)
@ColumnWidth(25)
@HeadRowHeight(25)
@HeadStyle
@HeadFontStyle(fontHeightInPoints = 12)
@Document(value = "document_zhifu_dianzi_result")
public class CaseVo {

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
    private Date refereeDate;

    @ExcelProperty(value = "案由", index = 4)
    private String cause;

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
    @ExcelIgnore
    private CaseSummaryVO summaryVO = null;

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

    @ExcelIgnore
    private List<CrimeVO> crimes = new ArrayList<>();

    @ExcelProperty(value = "HTML内容", index = 16)
    private String htmlContent;

    @ExcelProperty(value = "JSON内容", index = 17)
    private String json;
    @ExcelIgnore
    private JSONObject jsonContent;

    @ExcelIgnore
    private List<String> moneyString = new ArrayList<>();

    //诉讼请求
    @ExcelIgnore
    private String  litigationClaims;
    //判决结果
    @ExcelIgnore
    private String judgmentDesc;

    @ExcelIgnore
    private List<String> money = new ArrayList<>();

    @ExcelIgnore
    private String executionResult;

/*    @ExcelProperty(value = "立案日期", index = 18)
    private String registerCaseDate;

    @ExcelProperty(value = "租金", index = 19)
    private String disputedAmount;

    //违约金
    @ExcelProperty(value = "违约金", index = 20)
    private String iquidatedDamages;

    @ExcelProperty(value = "利息", index = 21)
    private String defaultInterest;

    @ExcelProperty(value = "是否一审终审", index = 22)
    private String finalInstance;

    @ExcelProperty(value = "二审是否改判", index = 23)
    private String change = "否";

    @ExcelProperty(value = "二审是否改判", index = 24)
    private String hearingFees;

    @ExcelProperty(value = "法院层级", index = 25)
    private String primary;

    @ExcelProperty(value = "是否使用简易程序", index = 26)
    private String summary = "否";

    @ExcelProperty(value = "是否使用简易程序", index = 27)
    private String small = "否";

    @ExcelProperty(value = "二审案号", index = 28)
    private String retrialCaseNo;*/



}
