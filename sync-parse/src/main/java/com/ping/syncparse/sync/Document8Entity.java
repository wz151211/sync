package com.ping.syncparse.sync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@Document(value = "document_8")
public class Document8Entity {

    @Id
    private String id;

    private String name;

    private String caseNo;

    private String courtName;

    private Date refereeDate;

    private String caseType;

    private String cause;

    private String party;

    private String keyword;

    private String trialProceedings;

    private String docType;

    private String htmlContent;

    private JSONObject jsonContent;

    private Date createTime;

    private String courtConsidered;

    private String judgmentResult;
}
