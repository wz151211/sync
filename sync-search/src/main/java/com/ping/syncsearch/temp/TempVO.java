package com.ping.syncsearch.temp;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2024/2/19 21:28
 */
@Data
@Document(value = "temp")
public class TempVO {
    private String code;
    private String name;
    private String count;
    @Field("id")
    private String id;
    private String province;
    private String city;
    private String county;
    private String value;

    private String year;
    private String type;
    private List<String> tree;

}
