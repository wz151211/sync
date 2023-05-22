package com.ping.syncmysqltomongo.task;

import com.ping.syncmysqltomongo.temp.TempService;
import com.ping.syncmysqltomongo.temp.ToMysqlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Async
@Slf4j
public class TaskTemp {
    @Autowired
    private TempService searchService;
    @Autowired
    private ToMysqlService toMysqlService;

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 10)
    public void save1() {
        try {
            toMysqlService.sync2();
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
