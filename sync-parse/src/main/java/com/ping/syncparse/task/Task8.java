package com.ping.syncparse.task;

import com.ping.syncparse.sync.Sync8Service;
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
public class Task8 {


    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();


    @Autowired
    private Sync8Service sync8Service;

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            sync8Service.sync1();
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
            sync8Service.sync2();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock2.unlock();
            }
        }
    }


}
