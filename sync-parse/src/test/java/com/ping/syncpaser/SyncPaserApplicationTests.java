package com.ping.syncpaser;

import com.ping.syncparse.SyncParseApplication;
import com.ping.syncparse.service.ExportMsService;
import com.ping.syncparse.service.ExportXsService;
import com.ping.syncparse.service.TempService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = SyncParseApplication.class)
@RunWith(SpringRunner.class)
class SyncPaserApplicationTests {

    @Autowired
    private ExportMsService msService;

    @Autowired
    private ExportXsService xsService;

    @Autowired
    private TempService tempService;

    @Test
    void contextLoads() {
    }

    @Test
    public void test1() {
        msService.export();
    }


    @Test
    public void test2() {
        xsService.export();
    }

    @Test
    public void test3() {
        tempService.convertMs();
    }

    @Test
    public void test4() {
        tempService.convertXs();
    }

    @Test
    public void test5() {
        tempService.convertXzxy();
    }

    @Test
    public void test6() {
        tempService.convertDq();
    }


}
