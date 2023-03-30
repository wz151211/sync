package com.ping.syncmysqltomongo.mysql.temp;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;

@Data
@TableName(value = "t_wenshu")
public class HnEntity {
    @TableId(type = IdType.AUTO)
    private Integer ID;

    @TableField("Title")
    private String name;

    @TableField("CaseNo")
    private String caseNo;

    @TableField("DocContent")
    private String docContent;

    @TableField("CourtName")
    private String courtName;

    @TableField("DocID")
    private String docId;

    @TableField("HtmlDocment")
    private String htmlDocment;

    @TableField("JudgeYear")
    private Integer judgeYear;

    @TableField("JudgeDate")
    private String judgeDate;

    @TableField("CreateTime")
    private Date createTime;

}
