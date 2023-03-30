package com.ping.syncparse.sync.c9;

import com.ping.syncparse.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 虚开发票
 */
@Data
@Document(value = "document_false_invoice")
public class DocumentFalseInvoiceEntity extends BaseEntity {
}
