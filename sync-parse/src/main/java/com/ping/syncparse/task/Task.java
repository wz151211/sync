package com.ping.syncparse.task;

import com.ping.syncparse.service.*;
import com.ping.syncparse.service.contract.ContractService;
import com.ping.syncparse.service.contract.ContractStatisticsService;
import com.ping.syncparse.service.contract.ExportContractService;
import com.ping.syncparse.service.contract.ParseContractService;
import com.ping.syncparse.service.divorce.ParseDivorceService;
import com.ping.syncparse.service.economic.EconomicService;
import com.ping.syncparse.service.economic.ExportEconomicService;
import com.ping.syncparse.service.gamble.ParseGambleService;
import com.ping.syncparse.service.invoice.InvoiceService;
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


    //   @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60*300L)
    public void easyExport() {
        exportService.export();
    }

    //  @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 10L)
    public void export() {
        exportTempService.export();
    }

    //  @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 3L)
    public void parse() {
        parsePartyEasyService.parse();
    }

    @Autowired
    private ParseGambleService parseGambleService;

    // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 3L)
    public void parseGamble() {
        parseGambleService.parse();
    }


    // @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 3L)
    public void update() {
        upadteTidService.update();
    }

    //  @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 3L)
    public void updateCaseNo() {
        upadteTidService.updateCaseNo();
    }

    @Autowired
    private ExportResultService exportResultService;

    // @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 60L)
    public void test12() {
        exportResultService.export();
    }

    @Autowired
    private ParseDivorceService parseDivorceService;

    // @Scheduled(initialDelay = 3 * 1000L, fixedRate = 1000 * 3L)
    public void divorce() {
        parseDivorceService.parse();
    }

    //@Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60)
    public void divorceExport() {
        exportMsService.export();
    }

    @Autowired
    private ParseContractService contractService;

    //@Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 3)
    public void contract() {
        contractService.parse();
    }

    @Autowired
    private ContractStatisticsService statisticsService;

    // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 3)
    public void statistics() {
        statisticsService.statistics();
    }

    @Autowired
    private ContractService contractService1;

    // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 3)
    public void ContractService() {
        contractService1.parse();
    }

    @Autowired
    private ExportContractService exportContractService;

    //@Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30)
    public void exportContractService() {
        exportContractService.export();
    }

    @Autowired
    private InvoiceService invoiceService;

    // @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30)
    private void invoiceService() {
        invoiceService.parse();
    }

    //   @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 300L)
    public void save3() {
        xsService.export();
    }

    @Autowired
    private EconomicService economicService;

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30)
    public void economicParse() {
        economicService.parse();
    }

    @Autowired
    private ExportEconomicService exportEconomicService;

    //  @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 30)
    public void exportEconomic() {
        exportEconomicService.export();
    }

}
