package com.ping.syncmysqlmongo.mongo.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:27
 */
@Data
@Document(value = "document_2015_2016")
public class Document2Entity extends BaseEntity {
}
