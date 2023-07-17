package com.ping.syncparse.service;

import lombok.Data;

@Data
public class CrimeVO {

    private String name;

    //是否单位犯罪
    private String unitCrime;

    private String eduLevel;

    private String fact;

    //罪名
    private String crime;

    //刑期
    private String imprisonmentTerm;

    //缓刑
    private String probation;

    private String isProbation;

    //罚金
    private String fine;

    //是否双罚制
    private String doublePenalty;

    private String content;
}
