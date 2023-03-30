package com.ping.syncparse.sync.c9;

import com.ping.syncparse.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 财政行政管理（财政）
 */
@Data
@Document(value = "document_finance")
public class DocumentFinanceEntity extends BaseEntity {
}
