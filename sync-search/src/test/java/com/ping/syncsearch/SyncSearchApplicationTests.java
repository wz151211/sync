package com.ping.syncsearch;

import com.ping.syncsearch.temp.TempMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SyncSearchApplicationTests {


    @Autowired
    private TempMapper tempMapper;

    @Test
    public void test1() {

        tempMapper.findCount();
    }

}
