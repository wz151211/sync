package com.ping.syncparse.service;

import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.PartyEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/12/31 15:21
 */
@Data
@Document(value = "documentm_xs_result")
public class CaseVo {

    @Id
    private String id;

    private String name;

    private String caseNo;

    private String courtName;

    private Date refereeDate;

    private String cause;

    private String trialProceedings;

    private String province;

    private List<PartyEntity> party = new ArrayList<>();

    private String marriagDate;

    private String haveChildren;

    private String remarry;

    private String judgmenResult;

    private List<CrimeVO> crimes = new ArrayList<>();

    private String partyContent;
    private String marriagContent;
    private String childrenContent;
    private String remarryContent;

    private String judgmenResultContent;

    private String htmlContent;

    private JSONObject jsonContent;


}
