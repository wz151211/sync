package com.ping.syncparse.entity;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "document_2016")
public class Document2016Entity extends BaseEntity {
}
