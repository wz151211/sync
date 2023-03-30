package com.ping.syncparse.sync.court;

import com.ping.syncparse.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 税务行政管理(税务）
 */
@Data
@Document(value = "document_court")
public class DocumentCourtEntity extends BaseEntity {
}
