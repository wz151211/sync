package com.ping.syncparse.task;


import com.ping.syncparse.sync.c140.SyncTempService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

/*@Component
@Async*/
@Slf4j
public class TaskTemp {
    @Autowired
    private SyncTempService tempService;

    // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        try {
            tempService.sync();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30L)
    public void save2() {
        try {
            tempService.syncTelecomFraud();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 30)
    public void save3() {
        try {
            tempService.syncInternetFraudEntity();
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
