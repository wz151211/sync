package com.ping.syncmysqltomongo.mongo.temp;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/12/15 22:26
 */
@Data
@TableName("party")
public class PartyEntity {

    @Id
    private String id;

    @TableField("case_id")
    private String caseId;

    @TableField("case_no")
    private String caseNo;

    private String type;

    private String name;

    private String sex;

    private String age;


    private String birthday;

    private String nation;

    private String province;

    private String city;

    private String county;

    private String address;

    @TableField("edu_level")
    private String eduLevel;

    private String profession;

    private String content;


}
