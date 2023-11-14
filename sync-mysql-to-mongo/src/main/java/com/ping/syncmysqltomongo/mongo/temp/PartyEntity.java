package com.ping.syncmysqltomongo.mongo.temp;


import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/12/15 22:26
 */
@Data
public class PartyEntity {

    @Id
    private String id;

    private String caseId;

    private String caseNo;

    private String type;

    private String name;

    private String sex;

    private String age;

    private String ageContent;

    private String birthday;

    private String nation;

    private String province;

    private String city;

    private String county;

    private String address;

    private String eduLevel;

    private String profession;

    private String idCard;

    private List<String> idCards = new ArrayList<>();

    private String content;

    //判决金额或诉请金额
    private String  petitionAmount;
    //判决金额或诉请金额内容
    private String  petitionAmountContent;



}
