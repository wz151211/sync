package com.ping.syncmysqltomongo.mysql.temp;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("area")
public class AreaEntity {
    private String id;
    private String pid;
    private String name;
    private int level;
    private String path;
    private String province;
    private String city;
    private String county;

}
