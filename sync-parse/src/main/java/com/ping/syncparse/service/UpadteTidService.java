package com.ping.syncparse.service;

import com.ping.syncparse.sync.c34.DocumentMsJtblEntity;
import com.ping.syncparse.sync.c34.DocumentMsMapper;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class UpadteTidService {

    @Autowired
    private DocumentMsMapper documentMsMapper;

    @Autowired
    private DocumentXsMapper documentXsMapper;
    private int pageSize = 20000;
    private AtomicInteger pageNum = new AtomicInteger(0);
    AtomicInteger count = new AtomicInteger();

    public void update() {
        List<DocumentXsLhEntity> entities = documentXsMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        entities.parallelStream().forEach(entity -> {
            log.info("{}", count.getAndIncrement());
            List<DocumentMsJtblEntity> byCaseNo = documentMsMapper.find(entity.getCaseNo());
            for (DocumentMsJtblEntity lh : byCaseNo) {
                if (entity.getCaseNo().equals(lh.getCaseNo())) {
                    continue;
                }
          /*      DocumentXsLhEntity xsLhEntity = new DocumentXsLhEntity();
                lh.setTId(entity.getCaseNo());
                BeanUtils.copyProperties(lh, xsLhEntity);*/
                lh.setTId(entity.getCaseNo());
                documentMsMapper.insert(lh);
            }
        });
    }

    public void updateCaseNo() {
        //  Criteria criteria = Criteria.where("caseNo").regex("解");
        Criteria criteria = Criteria.where("trialProceedings").is("民事二审");

        List<DocumentXsLhEntity> entities = documentXsMapper.findList(pageNum.get(), pageSize, criteria);
        pageNum.getAndIncrement();
        entities.parallelStream().forEach(entity -> {
            //    log.info("{}", count.getAndIncrement());
            String records = entity.getLitigationRecords();
            if (StringUtils.hasLength(records)) {
                records = records.replace("。", "，");
                records = records.replace(",", "，");
                records = records.replace("作出了", "");
                records = records.replace("作出", "");
                records = records.replace("作出的", "");
                records = records.replace("民事判决", "");
                records = records.replace("院的", "院");
                records = records.replace("日的", "日");
                for (String temp : records.split("，")) {
                    if (temp.contains("不服") && temp.contains("民") && (temp.contains("初") || temp.contains("重") || temp.contains("再") || temp.contains("终"))) {

                        int start = temp.indexOf("院（");
                        if (start == -1) {
                            start = temp.indexOf("日［");
                        }
                        if (start == -1) {
                            start = temp.indexOf("日以");
                        }
                        if (start == -1) {
                            start = temp.indexOf("日（");
                        }
                        if (start == -1) {
                            start = temp.indexOf("院〔");
                        }
                        if (start == -1) {
                            start = temp.indexOf("法（");
                        }
                        if (start == -1) {
                            start = temp.indexOf("县（");
                        }
                        if (start == -1) {
                            start = temp.indexOf("院﹝");
                        }
                        if (start == -1) {
                            start = temp.indexOf("并（");
                        }
                        if (start == -1) {
                            start = temp.indexOf("（");
                            start = start - 1;
                        }
                        if (start == -1) {
                            start = temp.indexOf("公诉");
                        }

                        int end = temp.indexOf("号");
                        if (end == -1) {
                            end = temp.indexOf("民事判决");
                        }
                        if (end > start) {
                            String caseNo = null;
                            try {
                                caseNo = temp.substring(start + 1, end + 1);
                                caseNo = caseNo.replace("（以下简称一审法院）", "");
                                caseNo = caseNo.replace("的", "");
                                caseNo = caseNo.replace("以", "");
                                log.info(caseNo);
                                entity.setTId(caseNo);
                                    documentXsMapper.insert(entity);
                            } catch (Exception e) {
                                log.info("诉讼记录={}", temp);
                                e.printStackTrace();
                            }
                            break;

                        } else if (start > 0 && end == -1) {
                            String caseNo = null;
                            try {
                                caseNo = temp.substring(start + 1);

                                caseNo = caseNo.replace("（以下简称一审法院）", "");
                                caseNo = caseNo.replace("的", "");
                                caseNo = caseNo.replace("以", "");
                                log.info(caseNo);
                                entity.setTId(caseNo);
                                    documentXsMapper.insert(entity);
                            } catch (Exception e) {
                                log.info("诉讼记录={}", temp);
                                e.printStackTrace();
                            }
                            break;

                        } else {
                            log.info("诉讼记录={}", temp);
                        }
                    }
                }
            }
        });
    }
}
