package com.ping.syncmongo.common.task;

import com.ping.syncmongo.local.UpdateDocumentService;
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
public class SaveTask1 {


    private final Lock lock1 = new ReentrantLock();
    private final Lock lock2 = new ReentrantLock();
    private final Lock lock3 = new ReentrantLock();
    private final Lock lock4 = new ReentrantLock();
    private final Lock lock5 = new ReentrantLock();
    private final Lock lock6 = new ReentrantLock();
    private final Lock lock7 = new ReentrantLock();
    private final Lock lock8 = new ReentrantLock();
    private final Lock lock9 = new ReentrantLock();
    private final Lock lock10 = new ReentrantLock();
    private final Lock lock11 = new ReentrantLock();
    private final Lock lock12 = new ReentrantLock();
    private final Lock lock13 = new ReentrantLock();
    private final Lock lock14 = new ReentrantLock();
    private final Lock lock15 = new ReentrantLock();
    private final Lock lock16 = new ReentrantLock();
    private final Lock lock17 = new ReentrantLock();
    private final Lock lock18 = new ReentrantLock();
    private final Lock lock19 = new ReentrantLock();
    private final Lock lock20 = new ReentrantLock();

    @Autowired
    private UpdateDocumentService updateDocumentService;

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save1() {
        boolean tryLock = false;
        try {
            tryLock = lock1.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
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
            updateDocumentService.save();
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
            updateDocumentService.save();
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
            updateDocumentService.save();
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
            updateDocumentService.save();
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
            updateDocumentService.save();
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
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock7.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 16 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save8() {
        boolean tryLock = false;
        try {
            tryLock = lock8.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock8.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 18 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save9() {
        boolean tryLock = false;
        try {
            tryLock = lock9.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock9.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 20 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save10() {
        boolean tryLock = false;
        try {
            tryLock = lock10.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock10.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 24 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save11() {
        boolean tryLock = false;
        try {
            tryLock = lock11.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock11.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 28 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save12() {
        boolean tryLock = false;
        try {
            tryLock = lock12.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock12.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 32 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save13() {
        boolean tryLock = false;
        try {
            tryLock = lock13.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock13.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 36 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save14() {
        boolean tryLock = false;
        try {
            tryLock = lock14.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock14.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 40 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save15() {
        boolean tryLock = false;
        try {
            tryLock = lock15.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock15.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 44 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save16() {
        boolean tryLock = false;
        try {
            tryLock = lock16.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock16.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 48 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save17() {
        boolean tryLock = false;
        try {
            tryLock = lock17.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock17.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 52 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save18() {
        boolean tryLock = false;
        try {
            tryLock = lock18.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock18.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 56 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save19() {
        boolean tryLock = false;
        try {
            tryLock = lock19.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock19.unlock();
            }
        }
    }

    @Scheduled(initialDelay = 60 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save20() {
        boolean tryLock = false;
        try {
            tryLock = lock20.tryLock(2, TimeUnit.SECONDS);
            updateDocumentService.save();
        } catch (Exception e) {
            log.error("", e);
        } finally {
            if (tryLock) {
                lock20.unlock();
            }
        }
    }
}
