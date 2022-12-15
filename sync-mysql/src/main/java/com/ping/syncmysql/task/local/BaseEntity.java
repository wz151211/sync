package com.ping.syncmysql.task.local;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class BaseEntity {
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField("docId")
    private String docId;

    @TableField("CourtInfo")
    private String courtInfo;

    @TableField("flag")
    private Integer flag;
}
