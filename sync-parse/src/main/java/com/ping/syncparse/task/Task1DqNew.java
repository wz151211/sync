package com.ping.syncparse.task;

import com.ping.syncparse.sync.c140.Sync140Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/*@Component
@Async*/
@Slf4j
public class Task1DqNew {
    @Autowired
    private Sync140Service searchService;

   // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        try {
            searchService.syncdq1();
        } catch (Exception e) {
            log.error("", e);
        }
    }


  //  @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2() {
        try {
            searchService.syncdq2();
        } catch (Exception e) {
            log.error("", e);
        }
    }

   // @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save3() {
        try {
            searchService.syncdq3();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save4() {
        try {
            searchService.syncdq4();
        } catch (Exception e) {
            log.error("", e);
        }
    }

   // @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save5() {
        try {
            searchService.syncdq4();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    //  @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save6() {
        try {
            searchService.syncdq4();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    //  @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save7() {
        try {
            searchService.syncdq4();
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
