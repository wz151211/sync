package com.ping.syncparse.service;

import com.ping.syncparse.sync.c34.DocumentMsJtblEntity;
import com.ping.syncparse.sync.c34.DocumentMsMapper;
import com.ping.syncparse.sync.c34.DocumentXsLhEntity;
import com.ping.syncparse.sync.c34.DocumentXsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class UpadteTidService {

    @Autowired
    private DocumentMsMapper documentMsMapper;

    @Autowired
    private DocumentXsMapper documentXsMapper;
    private int pageSize = 10000;
    private AtomicInteger pageNum = new AtomicInteger(0);
    AtomicInteger count = new AtomicInteger();

    public void update() {
        List<DocumentXsLhEntity> entities = documentXsMapper.findList(pageNum.get(), pageSize, null);
        pageNum.getAndIncrement();
        entities.parallelStream().filter(entity -> entity.getTrialProceedings().equals("民事一审")).forEach(entity -> {
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
}