package com.ping.syncsearch.entity;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@Document(value = "document_wuran")
public class DocumentTargetEntity extends JSONObject {

}
