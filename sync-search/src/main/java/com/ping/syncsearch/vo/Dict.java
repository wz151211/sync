package com.ping.syncsearch.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/8/27 00:12
 */
@Data
public class Dict {

    private String code;

    private String pId;

    private String name;

    private Integer level;

    private List<Dict> child = new ArrayList<>();
}
