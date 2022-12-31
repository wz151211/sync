package com.ping.syncparse.task;

import com.ping.syncparse.sync.c140.SyncDqService;
import com.ping.syncparse.sync.c140.SyncQjService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/*@Component
@Async*/
@Slf4j
public class Task1Dq {


    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();
    private final Lock lock3 = new ReentrantLock();
    private final Lock lock4 = new ReentrantLock();
    private final Lock lock5 = new ReentrantLock();
    private final Lock lock6 = new ReentrantLock();
    private final Lock lock7 = new ReentrantLock();

    @Autowired
    private SyncDqService syncDqService;

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            syncDqService.sync1();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock1.unlock();
            }
        }
    }


    @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2() {
        boolean tryLock = false;
        try {
            tryLock = lock2.tryLock(2, TimeUnit.SECONDS);
            syncDqService.sync2();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock2.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save3() {
        boolean tryLock = false;
        try {
            tryLock = lock3.tryLock(2, TimeUnit.SECONDS);
            syncDqService.sync3();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock3.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save4() {
        boolean tryLock = false;
        try {
            tryLock = lock4.tryLock(2, TimeUnit.SECONDS);
            syncDqService.sync4();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock4.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save5() {
        boolean tryLock = false;
        try {
            tryLock = lock5.tryLock(2, TimeUnit.SECONDS);
            syncDqService.sync5();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock5.unlock();
            }
        }
    }


    @Scheduled(initialDelay = 12 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save6() {
        boolean tryLock = false;
        try {
            tryLock = lock6.tryLock(2, TimeUnit.SECONDS);
            syncDqService.sync6();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock6.unlock();
            }
        }
    }


    @Scheduled(initialDelay = 14 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save7() {
        boolean tryLock = false;
        try {
            tryLock = lock7.tryLock(2, TimeUnit.SECONDS);
            syncDqService.sync7();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock7.unlock();
            }
        }
    }

}
