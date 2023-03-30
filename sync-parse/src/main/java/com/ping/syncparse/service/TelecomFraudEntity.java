package com.ping.syncparse.service;

import com.ping.syncparse.entity.BaseEntity;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(value = "document_telecom_fraud")
public class TelecomFraudEntity extends BaseEntity {
}
