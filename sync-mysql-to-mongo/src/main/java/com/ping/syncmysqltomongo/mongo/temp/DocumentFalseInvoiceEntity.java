package com.ping.syncmysqltomongo.mongo.temp;

import com.ping.syncmysqltomongo.mongo.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "document_false_invoice")
public class DocumentFalseInvoiceEntity extends BaseEntity {
}
