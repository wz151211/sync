package com.ping.syncmysqltomongo;

import org.junit.jupiter.api.Test;

public class Test1 {

    @Test
    public void test1() {
        String str = "执 行 裁 定 书".replace(" ", "");
        System.out.println(str.substring(0,2));
    }
}
