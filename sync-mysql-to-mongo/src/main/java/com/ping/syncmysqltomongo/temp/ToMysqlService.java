package com.ping.syncmysqltomongo.temp;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncmysqltomongo.mongo.entity.BaseEntity;
import com.ping.syncmysqltomongo.mongo.temp.*;
import com.ping.syncmysqltomongo.mysql.temp.PartyMapper;
import com.ping.syncmysqltomongo.mysql.temp.TempDocumentEntity;
import com.ping.syncmysqltomongo.mysql.temp.TempDocumentMapper;
import com.ping.syncmysqltomongo.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
public class ToMysqlService {
    @Autowired
    private TempDocumentMapper tempDocumentMapper;
    @Autowired
    private MongoTempMapper mongoTempMapper;

    @Autowired
    private DocumentTabMapper documentTabMapper;

    private AtomicInteger pageNum = new AtomicInteger(0);
    private Integer pageSize = 40000;

    public void sync() {
        log.info("pageNum={}", pageNum.get());
        List<MongoTempEntity> entities = mongoTempMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        for (MongoTempEntity entity : entities) {
            TempDocumentEntity tempDocument = new TempDocumentEntity();
            //    convert(entity, tempDocument);
            try {
                tempDocumentMapper.insert(tempDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }
/*            try {
                mongoTempMapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
    }

    public void sync2() {
        log.info("pageNum={}", pageNum.get());
        List<MongoTempEntity> entities = mongoTempMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        for (MongoTempEntity entity : entities) {
            TempDocumentEntity tempDocument = new TempDocumentEntity();
            // convert(entity, tempDocument);
            BaseEntity base = BeanUtils.toEntity(entity);
            convert(base, tempDocument);
            try {
                tempDocumentMapper.insert(tempDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }
/*            try {
                mongoTempMapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
    }

    @Autowired
    private DocumentFinanceMapper financeMapper;
    @Autowired
    private PartyMapper partyMapper;

    public void sync3() {
        log.info("pageNum={}", pageNum.get());
        List<DocumentFinanceEntity> entities = financeMapper.findList(0, pageSize, null);
        pageNum.getAndIncrement();
        for (DocumentFinanceEntity entity : entities) {
            TempDocumentEntity tempDocument = new TempDocumentEntity();
            // convert(entity, tempDocument);
            // BaseEntity base = BeanUtils.toEntity(entity);
            org.springframework.beans.BeanUtils.copyProperties(entity, tempDocument);
            convert1(entity, tempDocument);
            for (PartyEntity partyEntity : entity.getParty()) {
                partyEntity.setId(UUID.randomUUID().toString().replace("-", ""));
                partyEntity.setCaseId(entity.getId());
                try {
                    partyMapper.insert(partyEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                tempDocumentMapper.insert(tempDocument);
                mongoTempMapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void sync4() {
        log.info("pageNum={}", pageNum.get());
        List<DocumentTabEntity> entities = documentTabMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        for (DocumentTabEntity entity : entities) {
            TempDocumentEntity tempDocument = new TempDocumentEntity();
            // convert(entity, tempDocument);
            // BaseEntity base = BeanUtils.toEntity(entity);
            convert(entity, tempDocument);
            try {
                tempDocumentMapper.insert(tempDocument);
            } catch (Exception e) {
                e.printStackTrace();
            }
/*            try {
                mongoTempMapper.delete(entity.getId());
            } catch (Exception e) {
                e.printStackTrace();
            }*/

        }
    }

    private void convert(BaseEntity from, TempDocumentEntity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
      /*  if (from.getJsonContent() != null) {
            to.setJsonContent(from.getJsonContent().toJSONString());
        }*/
        if (from.getRefereeDate() != null) {
            to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), -8));
        }
        to.setCaseType(from.getCaseType());
        if (from.getCause() != null && from.getCause().size() > 0) {
            to.setCause(from.getCause().stream().map(Object::toString).collect(Collectors.joining(",")));
        }
        if (from.getParty() != null && from.getParty().size() > 0) {
            to.setParty(from.getParty().stream().map(Object::toString).collect(joining(",")));
        }

        if (from.getKeyword() != null && from.getKeyword().size() > 0) {
            to.setKeyword(from.getKeyword().stream().map(Object::toString).collect(joining(",")));
        }

        if (from.getLegalBasis() != null && from.getLegalBasis().size() > 0) {
            to.setLegalBasis(from.getLegalBasis().stream().map(c -> {
                JSONObject aa = JSONObject.parseObject(JSON.toJSONString(c));
                return aa.getString("fgmc") + aa.getString("tkx");
            }).collect(joining(",")));
        }
        to.setTrialProceedings(from.getTrialProceedings());
        to.setDocType(from.getDocType());
        //to.setHtmlContent(from.getHtmlContent());
        to.setJudgmentResult(from.getJudgmentResult());
        to.setCourtConsidered(from.getCourtConsidered());
        to.setLitigationRecords(from.getLitigationRecords());
        to.setFact(from.getFact());
        to.setProvince(from.getProvince());
        to.setCity(from.getCity());
        to.setCounty(from.getCounty());

    }

    private void convert1(DocumentFinanceEntity from, TempDocumentEntity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
     /*   if (from.getJsonContent() != null) {
            to.setJsonContent(from.getJsonContent().toJSONString());
        }*/
        if (from.getRefereeDate() != null) {
            to.setRefereeDate(DateUtil.offsetHour(from.getRefereeDate(), -8));
        }
        to.setCaseType(from.getCaseType());
        to.setCause(from.getCause());
        if (from.getParty() != null && from.getParty().size() > 0) {
            List<PartyEntity> list = new ArrayList<>();
            for (PartyEntity entity : from.getParty()) {
                PartyEntity partyEntity = new PartyEntity();
                partyEntity.setId(entity.getId());
                partyEntity.setCaseNo(entity.getCaseNo());
                partyEntity.setType(entity.getType());
                partyEntity.setName(entity.getName());
                //   partyEntity.setIdCard(null);
                partyEntity.setContent(entity.getContent());
                list.add(partyEntity);
            }
            to.setParty(JSON.toJSONString(list));
        }
        to.setKeyword(from.getKeyword());
        to.setLegalBasis(from.getLegalBasis());
        to.setTrialProceedings(from.getTrialProceedings());
        to.setDocType(from.getDocType());
        //    to.setHtmlContent(from.getHtmlContent());
        //   to.setJudgmentResult(from.getJudgmentResult());
        to.setCourtConsidered(from.getCourtConsidered());
        to.setLitigationRecords(from.getLitigationRecords());
        to.setFact(from.getFact());
        to.setProvince(from.getProvince());
        to.setCity(from.getCity());
        to.setCounty(from.getCounty());

    }
}
