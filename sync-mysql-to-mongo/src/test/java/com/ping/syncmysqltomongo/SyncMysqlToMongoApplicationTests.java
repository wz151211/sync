package com.ping.syncmysqltomongo;

import com.ping.syncmysqltomongo.temp.TempService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class SyncMysqlToMongoApplicationTests {
@Autowired
private TempService tempService;
    @Test
    void contextLoads() {
        tempService.sync();
    }

}
