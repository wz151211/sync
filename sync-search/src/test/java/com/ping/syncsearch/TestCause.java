package com.ping.syncsearch;

import com.ping.syncsearch.utils.CauseUtils;
import org.junit.jupiter.api.Test;

public class TestCause {

    @Test
    public void test1(){
        System.out.println(CauseUtils.getCauseList("118"));
    }
}
