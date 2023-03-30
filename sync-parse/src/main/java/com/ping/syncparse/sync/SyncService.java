package com.ping.syncparse.sync;

import cn.hutool.core.date.DateUtil;
import com.ping.syncparse.entity.*;
import com.ping.syncparse.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

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
    private Document2014Mapper document2014Mapper;
    @Autowired
    private Document2015Mapper document2015Mapper;
    @Autowired
    private Document2016Mapper document2016Mapper;
    @Autowired
    private Document2017Mapper document2017Mapper;
    @Autowired
    private Document2018Mapper document2018Mapper;
    @Autowired
    private Document2019Mapper document2019Mapper;
    @Autowired
    private Document2020Mapper document2020Mapper;
    @Autowired
    private Document2021Mapper document2021Mapper;
    @Autowired
    private Document2022Mapper document2022Mapper;
    @Autowired
    private Document2023Mapper document2023Mapper;
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
        for (DocumentEntity c : list) {


            if (c.getRefereeDate() != null && c.getJsonContent() != null) {
                if (DateUtil.parse("2014-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2014Entity entity = new Document2014Entity();
                    toEntity(c, entity);
                    try {
                        document2014Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2015-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2015-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2015Entity entity = new Document2015Entity();
                    toEntity(c, entity);
                    try {
                        document2015Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2016-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2016-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2016Entity entity = new Document2016Entity();
                    toEntity(c, entity);
                    try {
                        document2016Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2017-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2017-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2017Entity entity = new Document2017Entity();
                    toEntity(c, entity);
                    try {
                        document2017Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2018-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2018-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2018Entity entity = new Document2018Entity();
                    toEntity(c, entity);
                    try {
                        document2018Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2019-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2019-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2019Entity entity = new Document2019Entity();
                    toEntity(c, entity);
                    try {
                        document2019Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2020-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2020-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2020Entity entity = new Document2020Entity();
                    toEntity(c, entity);
                    try {
                        document2020Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2021-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2021-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2021Entity entity = new Document2021Entity();
                    toEntity(c, entity);
                    try {
                        document2021Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2022-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2022-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2022Entity entity = new Document2022Entity();
                    toEntity(c, entity);
                    try {
                        document2022Mapper.insert(entity);
                    } catch (DuplicateKeyException ex) {
                        log.info("已存在id={}", entity.getId());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (DateUtil.parse("2023-01-01 00:00:00").isBefore(c.getRefereeDate())
                        && DateUtil.parse("2023-12-31 23:59:59").isAfter(c.getRefereeDate())) {
                    Document2023Entity entity = new Document2023Entity();
                    toEntity(c, entity);
                    try {
                        document2023Mapper.insert(entity);
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
