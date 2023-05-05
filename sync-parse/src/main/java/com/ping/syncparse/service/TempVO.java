package com.ping.syncparse.service;

import com.alibaba.fastjson.JSONObject;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(value = "document_dm")
public class TempVO extends JSONObject {
}
