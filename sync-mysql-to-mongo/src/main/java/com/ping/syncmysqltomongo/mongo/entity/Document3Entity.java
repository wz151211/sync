package com.ping.syncmysqltomongo.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@Document(value = "document_2017")
public class Document3Entity extends BaseEntity {

}
