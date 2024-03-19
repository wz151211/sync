package com.ping.syncsearch.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(value = "area")
public class AreaEntity {
    @Id
    private String id;
    private String pid;
    private String name;
    private int level;
    private String path;
    private String province;
    private String city;
    private String county;

}
