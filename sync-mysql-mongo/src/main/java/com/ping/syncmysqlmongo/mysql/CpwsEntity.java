package com.ping.syncmysqlmongo.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "cpws")
public class CpwsEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("docId")
    private String docId;

    @TableField("CourtInfo")
    private String courtInfo;

    @TableField("flag")
    private Integer flag;

}
