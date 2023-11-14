package com.ping.syncmongo.local.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "cpws")
public class CpwsEntity {
    @Id
    private String oId;

    private Integer id;
    private String docId;

    private String CourtInfo;

    private Integer flag;
}
