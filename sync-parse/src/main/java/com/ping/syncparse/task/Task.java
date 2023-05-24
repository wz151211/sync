package com.ping.syncparse.task;

import com.ping.syncparse.service.*;
import com.ping.syncparse.service.divorce.ParseDivorceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Async
@Slf4j
public class Task {


    private final Lock lock1 = new ReentrantLock();


    @Autowired
    private ParsePartyService parsePartyService;

    @Autowired
    private ParsePartyEasyService parsePartyEasyService;
    @Autowired
    private ExportTempService exportTempService;

    @Autowired
    private ExportEasyService exportEasyService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ExportMsService exportMsService;
    @Autowired
    private ExportXsService xsService;

    @Autowired
    private TempService tempService;

    @Autowired
    private UpadteTidService upadteTidService;

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

    //  @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30)
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

    // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60L)
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

   // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 10L)
    public void export() {
        exportTempService.export();
    }

    // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60*300L)
    public void easyExport() {
        exportService.export();
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

  //  @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 3L)
    public void parse() {
        parsePartyEasyService.parse();
    }

    //  @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 3L)
    public void update() {
        upadteTidService.update();
    }

    @Autowired
    private ExportResultService exportResultService;

      @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 3L)
    public void test12() {
        exportResultService.export();
    }

    @Autowired
    private ParseDivorceService parseDivorceService;

   // @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 3L)
    public void divorce() {
        parseDivorceService.parse();
    }
}
