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
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.*;

@Data
@Document(value = "document_sf_result")
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

    private String knowWayContent;

    //相识日期
    private String knowDate;
    private String knowDateContent;

    //订婚日期
    private String engaged;
    private String engagedContent;

    private String engagedDate;
    private String engagedDateContent;

    //是否同居
    private String liveTogether;
    private String liveTogetherContent;
    //是否办理结婚登记
    private String marriageRegistration;
    private String marriageRegistrationContent;

    //是否办理结婚登记
    private String marriageRegistrationDate;
    private String marriageRegistrationDateContent;

    //是否举办婚礼
    private String hostingWeddingDate;
    private String hostingWeddingDateContent;

    private String hostingWedding;
    private String hostingWeddingContent;

    //解除关系
    private String dissolveRelationshipDate;
    private String dissolveRelationshipDateContent;

    //流产
    private String abort;
    private String abortContent;

    //是否有孩子
    private String child;
    private String childContent;
    //彩礼数额
    private Set<String> bridePriceTotal = new LinkedHashSet<>();
    private Set<String> bridePriceTotalContent = new LinkedHashSet<>();

    //彩礼是否包含首饰三金
    private String bridePriceGold;
    private String bridePriceGoldContent;

    //彩礼是否包含首饰三金
    private String bridePriceCar;
    private Set<String> bridePriceCarContent = new LinkedHashSet<>();
    //彩礼是否包含房子
    private String bridePriceHouse;
    private Set<String> bridePriceHouseContent = new LinkedHashSet<>();

    //彩礼来源
    private Set<String> bridePriceFrom = new LinkedHashSet<>();

    //彩礼去向
    private Set<String> bridePriceTo = new LinkedHashSet<>();

    //彩礼原文表述
    private String bridePriceText;

    //是否提到生活困难
    private String bridePricePoverty;
    private String bridePricePovertyContent;

    //是否提到生活困难
    private String bridePriceIndebted;
    private String bridePriceIndebtedContent;

    //判决彩礼返还金额
    private String bridePriceReturn;
    private String bridePriceReturnContent;

    //最终彩礼认定句
    private String bridePriceNote;
}
