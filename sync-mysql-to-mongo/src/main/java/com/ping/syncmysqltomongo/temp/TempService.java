package com.ping.syncmysqltomongo.temp;

import cn.hutool.core.date.DateUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncmysqltomongo.mongo.entity.BaseEntity;
import com.ping.syncmysqltomongo.mongo.temp.*;
import com.ping.syncmysqltomongo.mysql.temp.TempDocumentEntity;
import com.ping.syncmysqltomongo.mysql.temp.TempDocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class TempService {

    @Autowired
    private DocumentEvasionMapper evasionMapper;
    @Autowired
    private DocumentFalseInvoiceMapper falseInvoiceMapper;
    @Autowired
    private DocumentFinanceMapper financeMapper;
    @Autowired
    private DocumentRevenueMapper revenueMapper;
    @Autowired
    private DocumentBetrothalMapper betrothalMapper;
    @Autowired
    private TempDocumentMapper tempDocumentMapper;
    @Autowired
    private DocumentTabMapper tabMapper;
    private AtomicInteger pageNumEvasion = new AtomicInteger(0);

    public void sync() {
        log.info("pageNum={}", pageNumEvasion.get());
        List<TempDocumentEntity> tempDocumentEntities = tempDocumentMapper.selectList(Wrappers.<TempDocumentEntity>lambdaQuery().eq(TempDocumentEntity::getCause, "盗掘古文化遗址、古墓葬").last("limit " + (pageNumEvasion.get() * 3000) + ", 3000"));
        pageNumEvasion.getAndIncrement();
        for (TempDocumentEntity entity : tempDocumentEntities) {
            if (StringUtils.hasLength(entity.getCaseNo()) && StringUtils.hasLength(entity.getName()) && entity.getRefereeDate() != null) {
                DocumentEvasionEntity evasionEntity = new DocumentEvasionEntity();
                BeanUtils.copyProperties(entity, evasionEntity);
                convert(entity, evasionEntity);
                try {
                    evasionMapper.save(evasionEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }



    private void convert(TempDocumentEntity entity, BaseEntity base) {
        if (StringUtils.hasLength(entity.getJsonContent())) {
            base.setJsonContent(JSON.parseObject(entity.getJsonContent()));
        }
        if (StringUtils.hasText(entity.getCause())) {
            String[] split = entity.getCause().split(",");
            base.setCause(JSON.parseArray(JSON.toJSONString(split)));
        }
        if (StringUtils.hasText(entity.getParty())) {
            String party = entity.getParty();
            party = party.replace("；", ",");
            party = party.replace(";", ",");
            base.setParty(JSON.parseArray(JSON.toJSONString(party.split(","))));
        }
        if (StringUtils.hasText(entity.getKeyword())) {
            String[] split = entity.getKeyword().split(",");
            base.setKeyword(JSON.parseArray(JSON.toJSONString(split)));
        }
        String uid = entity.getName().trim() + entity.getCaseNo().trim() + entity.getRefereeDate().getTime();
        if (entity.getRefereeDate() != null) {
            base.setRefereeDate(DateUtil.offsetHour(entity.getRefereeDate(), 8));
        }
        String md5 = MD5.create().digestHex(uid);
        log.info("uid={}", uid);
        log.info("md5={}", md5);
       // base.setUid(md5);
        if (entity.getRefereeDate() != null) {
            base.setRefereeDate(DateUtil.offsetHour(entity.getRefereeDate(), 8));
        }
    }
}
