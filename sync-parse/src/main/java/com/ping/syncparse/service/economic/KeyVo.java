package com.ping.syncparse.service.economic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Builder;

@Builder
public class KeyVo {
    @JSONField(ordinal = 1)
    public String title;
    @JSONField(ordinal = 2)
    public String content;
}
