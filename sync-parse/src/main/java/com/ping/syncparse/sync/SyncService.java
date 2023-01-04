package com.ping.syncparse.sync;

import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class SyncService {
    @Autowired
    private DocumentMapper documentMapper;
    @Autowired
    private Document1Mapper document1Mapper;
    @Autowired
    private Document2Mapper document2Mapper;
    @Autowired
    private Document3Mapper document3Mapper;
    @Autowired
    private Document4Mapper document4Mapper;
    @Autowired
    private Document5Mapper document5Mapper;
    @Autowired
    private Document6Mapper document6Mapper;
    @Autowired
    private Document7Mapper document7Mapper;
    @Autowired
    private DocumentOtherMapper documentOtherMapper;

    private AtomicInteger pageNum = new AtomicInteger(-1);

    @Value("${pageNum}")
    private Integer page;

    private Integer pageSize = 10000;
    private Set<String> years = new HashSet<>();

    {
        for (int i = 1990; i <= 2014; i++) {
            years.add(i + "");
        }
    }

    public void sync() {
        if (pageNum.get() < page) {
            pageNum.set(page);
        }
        pageNum.getAndIncrement();
		        log.info("pageNum={}", pageNum);

        List<DocumentEntity> list = documentMapper.findList(pageNum.get(), pageSize, null);
		for(DocumentEntity c : list){
                        

            if (c.getRefereeDate() != null) {
                if (DateUtil.parse("2014-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document1Entity entity = new Document1Entity();
                    toEntity(c, entity);
                    try {
                        document1Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (DateUtil.parse("2015-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2016-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2Entity entity = new Document2Entity();
                    toEntity(c, entity);
                    try {
                        document2Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2017-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2017-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document3Entity entity = new Document3Entity();
                    toEntity(c, entity);
                    try {
                        document3Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2018-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2018-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document4Entity entity = new Document4Entity();
                    toEntity(c, entity);
                    try {
                        document4Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2019-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2019-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document5Entity entity = new Document5Entity();
                    toEntity(c, entity);
                    try {
                        document5Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2020-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2020-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document6Entity entity = new Document6Entity();
                    toEntity(c, entity);
                    try {
                        document6Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2021-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2022-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document7Entity entity = new Document7Entity();
                    toEntity(c, entity);
                    try {
                        document7Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else if (StringUtils.hasText(c.getCaseNo())) {
                if (c.getCaseNo().contains("2015") || c.getCaseNo().contains("2016")) {
                    Document2Entity entity = new Document2Entity();
                    toEntity(c, entity);
                    try {
                        document2Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (c.getCaseNo().contains("2017")) {
                    Document3Entity entity = new Document3Entity();
                    toEntity(c, entity);
                    try {
                        document3Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (c.getCaseNo().contains("2018")) {
                    Document4Entity entity = new Document4Entity();
                    toEntity(c, entity);
                    try {
                        document4Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (c.getCaseNo().contains("2019")) {
                    Document5Entity entity = new Document5Entity();
                    toEntity(c, entity);
                    try {
                        document5Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (c.getCaseNo().contains("2020")) {
                    Document6Entity entity = new Document6Entity();
                    toEntity(c, entity);
                    try {
                        document6Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (c.getCaseNo().contains("2021") || c.getCaseNo().contains("2022")) {
                    Document7Entity entity = new Document7Entity();
                    toEntity(c, entity);
                    try {
                        document7Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Document1Entity entity = new Document1Entity();
                    toEntity(c, entity);
                    try {
                        document1Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } else {
                DocumentOtherEntity entity = new DocumentOtherEntity();
                toEntity(c, entity);
                try {
                    documentOtherMapper.insert(entity);
                } catch (DuplicateKeyException ex) {
                    log.info("已存在id={}", entity.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    private void toEntity(DocumentEntity from, BaseEntity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
        if (from.getRefereeDate() != null) {
            to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), 8));
        }
        to.setCaseType(from.getCaseType());
        to.setCause(from.getCause());
        to.setParty(from.getParty());
        to.setKeyword(from.getKeyword());
        to.setTrialProceedings(from.getTrialProceedings());
        to.setDocType(from.getDocType());
        to.setHtmlContent(from.getHtmlContent());
        to.setJsonContent(from.getJsonContent());
        to.setCreateTime(new Date());
    }
}
