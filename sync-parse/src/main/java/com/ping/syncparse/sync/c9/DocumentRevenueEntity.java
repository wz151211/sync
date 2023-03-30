package com.ping.syncparse.sync.c9;

import com.ping.syncparse.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 危害税收征管罪
 */
@Data
@Document(value = "document_revenue")
public class DocumentRevenueEntity extends BaseEntity {
}
