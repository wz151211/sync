package com.ping.syncsearch.temp;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author: W.Z
 * @Date: 2024/2/19 22:18
 */
@Data
@Document(value = "temp_minshi")
public class TempData {
    private String province;
    private String city;
    private String county;

    private String name;
    private String code;

    private String c2010;
    private String c2011;
    private String c2012;
    private String c2013;
    private String c2014;
    private String c2015;
    private String c2016;
    private String c2017;
    private String c2018;
    private String c2019;
    private String c2020;
    private String c2021;
    private String c2022;
    private String c2023;
}
