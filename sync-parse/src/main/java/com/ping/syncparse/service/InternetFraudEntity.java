package com.ping.syncparse.service;

import com.ping.syncparse.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "document_hn")
public class InternetFraudEntity extends BaseEntity {
}
