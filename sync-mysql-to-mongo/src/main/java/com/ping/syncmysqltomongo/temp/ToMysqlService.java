package com.ping.syncmysqltomongo.temp;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncmysqltomongo.mongo.temp.MongoTempEntity;
import com.ping.syncmysqltomongo.mongo.temp.MongoTempMapper;
import com.ping.syncmysqltomongo.mysql.temp.TempDocumentEntity;
import com.ping.syncmysqltomongo.mysql.temp.TempDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    private AtomicInteger pageNum = new AtomicInteger(0);
    private Integer pageSize = 10000;

    public void sync() {
        log.info("pageNum={}", pageNum.get());
        List<MongoTempEntity> entities = mongoTempMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        for (MongoTempEntity entity : entities) {
            TempDocumentEntity tempDocument = new TempDocumentEntity();
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

    private void convert(MongoTempEntity from, TempDocumentEntity to) {
        to.setId(from.getId());
        to.setName(from.getName());
        to.setCaseNo(from.getCaseNo());
        to.setCourtName(from.getCourtName());
        if (from.getJsonContent() != null) {
            to.setJsonContent(from.getJsonContent().toJSONString());
        }
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
        to.setHtmlContent(from.getHtmlContent());
        to.setJudgmentResult(from.getJudgmentResult());
        to.setCourtConsidered(from.getCourtConsidered());
        to.setLitigationRecords(from.getLitigationRecords());
        to.setFact(from.getFact());
        to.setProvince(from.getProvince());
        to.setCity(from.getCity());
        to.setCounty(from.getCounty());

    }
}