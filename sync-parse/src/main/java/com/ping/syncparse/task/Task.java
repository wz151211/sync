package com.ping.syncparse.task;

import com.ping.syncparse.service.ExportMsService;
import com.ping.syncparse.service.ExportXsService;
import com.ping.syncparse.service.ParsePartyService;
import com.ping.syncparse.service.TempService;
import com.ping.syncparse.sync.c140.Sync140Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//@Component
//@Async
@Slf4j
public class Task {


    private final Lock lock1 = new ReentrantLock();


    @Autowired
    private ParsePartyService parsePartyService;
    @Autowired
    private ExportMsService exportMsService;
    @Autowired
    private ExportXsService xsService;

    @Autowired
    private TempService tempService;

   // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30)
    public void saveTemp() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            tempService.convert();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock1.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30)
    public void count() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            tempService.count();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock1.unlock();
            }
        }
    }

    //  @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 300L)
    public void save1() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            parsePartyService.parse();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock1.unlock();
            }
        }
    }

    //  @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 300L)
    public void save2() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            exportMsService.export();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock1.unlock();
            }
        }
    }

    //    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 300L)
    public void save3() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            xsService.export();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock1.unlock();
            }
        }
    }

}
