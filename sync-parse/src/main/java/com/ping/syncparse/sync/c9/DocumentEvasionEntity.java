package com.ping.syncparse.sync.c9;

import com.ping.syncparse.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 偷税
 */
@Data
@Document(value = "document_evasion")
public class DocumentEvasionEntity extends BaseEntity {
}
