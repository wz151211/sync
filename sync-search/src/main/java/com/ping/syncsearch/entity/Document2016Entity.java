package com.ping.syncsearch.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "ws_2016")
public class Document2016Entity extends JSONObject {
}
