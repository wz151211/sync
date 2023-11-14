package com.ping.syncmysqltomongo.mongo.temp;

import com.alibaba.fastjson.JSONObject;
import com.ping.syncmysqltomongo.mongo.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "document_lihunjiufen")
public class MongoTempEntity extends JSONObject {
}
