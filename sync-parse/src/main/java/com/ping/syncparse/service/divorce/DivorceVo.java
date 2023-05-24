package com.ping.syncparse.service.divorce;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.format.DateTimeFormat;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.service.CaseSummaryVO;
import com.ping.syncparse.service.CrimeVO;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class DivorceVo {

    @Id
    private String id;

    private String tId;

    private String name;

    private String caseNo;

    private String courtName;

    @DateTimeFormat("yyyy-MM-dd")
    private Date refereeDate;

    private String cause;

    private String caseType;

    private String docType;

    private String trialProceedings;

    private String province;

    private String city;

    private String county;


    private List<PartyEntity> party = new ArrayList<>();

    private CaseSummaryVO summaryVO = null;

    private String judgmentResult;

    private String courtConsidered;

    private String litigationRecords;

    private String legalBasis;

    private String legalBasisCount;

    private String fact;


    private List<CrimeVO> crimes = new ArrayList<>();

    //@ExcelProperty(value = "HTML内容", index = 16)

    private String htmlContent;

    //  @ExcelProperty(value = "JSON内容", index = 17)

    private String json;

    private JSONObject jsonContent;


    //诉讼请求

    private String litigationClaims;
    //判决结果

    private String judgmentDesc;


    private List<String> money = new ArrayList<>();


    private String executionResult;

    private String hearingFees;
    //被告受理费

    private String defendantHearingFees;
    //原告受理费

    private String plaintiffHearingFees;

    //相识方式
    private String knowWay;

    //相识日期
    private String knowDate;

    //订婚日期
    private String engagedDate;

    //是否同居
    private String liveTogether;
    //是否办理结婚登记
    private String marriageRegistration;

    //是否办理结婚登记
    private String marriageRegistrationDate;

    //是否举办婚礼
    private String hostingWedding;

    //解除关系
    private String dissolveRelationshipDate;

    //流产
    private String abort;

    //是否有孩子
    private String child;
    //是否涉及彩礼
    private String bridePrice;
    //彩礼数额
    private String bridePriceTotal;

    //彩礼是否包含首饰三金
    private String bridePriceGold;

    //彩礼是否包含首饰三金
    private String bridePriceCar;
    //彩礼是否包含房子
    private String bridePriceHouse;

    //彩礼来源
    private String bridePriceFrom;

    //彩礼去向
    private String bridePriceTo;

    //彩礼原文表述
    private String bridePriceText;

    //是否提到生活困难
    private String bridePricePoverty;

    //是否提到生活困难
    private String bridePriceIndebted;

    //判决彩礼返还金额
    private String bridePriceReturn;

    //最终彩礼认定句
    private String bridePriceNote;
}
