package com.ping.syncsearch.temp;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: W.Z
 * @Date: 2023/10/12 15:51
 */
@Data
@Document(value = "ws_2014")
public class TempEntity extends JSONObject {

}
