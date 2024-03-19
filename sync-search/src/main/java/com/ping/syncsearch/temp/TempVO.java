package com.ping.syncsearch.temp;

import lombok.Data;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2024/2/19 21:28
 */
@Data
public class TempVO {
    private String code;
    private String name;
    private String count;
    private List<String> tree;

}
