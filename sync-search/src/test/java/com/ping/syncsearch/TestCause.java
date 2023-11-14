package com.ping.syncsearch;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ping.syncsearch.utils.CauseUtils;
import org.junit.jupiter.api.Test;

import java.util.Set;

public class TestCause {

    @Test
    public void test1() {
        Set<String> causeList = CauseUtils.getCauseList("9299");
        System.out.println(JSON.toJSONString(causeList));


    }
}
