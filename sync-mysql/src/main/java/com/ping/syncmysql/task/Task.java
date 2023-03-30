package com.ping.syncmysql.task;

import com.ping.syncmysql.temp.ProtectionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Async
@Slf4j
public class Task {
    @Autowired
    private ProtectionService protectionService;

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 60 * 24L)
    public void save4() {
        boolean tryLock = false;
        try {
            System.out.println("-----------");
            protectionService.update();
        } catch (Exception e) {
            log.error("", e);
        }
    }

    @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 60 * 24L)
    public void save5() {
        boolean tryLock = false;
        try {
            System.out.println("-----------");

            protectionService.updateRelated();
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
