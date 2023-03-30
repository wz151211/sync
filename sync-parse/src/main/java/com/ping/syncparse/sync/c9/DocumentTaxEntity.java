package com.ping.syncparse.sync.c9;

import com.ping.syncparse.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 税务行政管理(税务）
 */
@Data
@Document(value = "document_tab")
public class DocumentTaxEntity extends BaseEntity {
}
