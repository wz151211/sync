package com.ping.syncparse.sync.c8;

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
@Document(value = "document_8_new_new")
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
