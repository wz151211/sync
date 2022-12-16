package com.ping.syncparse.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@TableName(value = "document")
public class DocumentEntity {

    @TableId(type = IdType.INPUT)
    private String id;

    @TableField("name")
    private String name;

    @TableField("case_no")
    private String caseNo;

    @TableField("court_name")
    private String courtName;

    @TableField("referee_date")
    private String refereeDate;


    @TableField("case_type")
    private String caseType;



    @TableField("html_content")
    private String htmlContent;

    @TableField("json_content")
    private String jsonContent;

}
