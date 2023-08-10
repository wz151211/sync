package com.ping.syncparse.service;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@Document(value = "doc_nuoyongzijin")
public class DocumentTargetEntity extends JSONObject {

}
