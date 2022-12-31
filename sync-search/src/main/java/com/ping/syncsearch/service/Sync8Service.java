package com.ping.syncparse.sync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.Document1Entity;
import com.ping.syncparse.entity.DocumentEntity;
import com.ping.syncparse.mapper.Document1Mapper;
import com.ping.syncparse.mapper.DocumentMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.Collectors.joining;

@Service
@Slf4j
public class Sync8Service {


    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private Document1Mapper document1Mapper;

    @Autowired
    private Document8Mapper document8Mapper;
    private AtomicInteger pageNum1 = new AtomicInteger(0);
    private AtomicInteger pageNum2 = new AtomicInteger(0);

    private final int pageSize = 100;

    public void sync1() {
        Criteria criteria = Criteria
                .where("caseType").is("行政案件");
        criteria.and("htmlContent").regex("行政协议");
        log.info("pageNum={}", pageNum1.get());
        List<DocumentEntity> list = documentMapper.findList(pageNum1.get(), pageSize, criteria);
        pageNum1.getAndIncrement();
        log.info("size={}", list.size());
        for (DocumentEntity entity : list) {
            Document8Entity document8Entity = new Document8Entity();
            BeanUtils.copyProperties(entity, document8Entity);
            JSONObject jsonContent = entity.getJsonContent();
            if (jsonContent != null) {
                String courtConsidered = jsonContent.getString("s26");
                String judgmentResult = jsonContent.getString("s27");
                document8Entity.setCourtConsidered(courtConsidered);
                document8Entity.setJudgmentResult(judgmentResult);
                JSONArray causes = jsonContent.getJSONArray("s11");
                String cause = null;
                if (causes != null) {
                    cause = causes.stream().map(Object::toString).collect(joining(","));
                    document8Entity.setCause(cause);
                }
                JSONArray partys = jsonContent.getJSONArray("s17");
                String party = null;
                if (partys != null) {
                    party = partys.stream().map(Object::toString).collect(joining(","));
                    document8Entity.setParty(party);
                }
                JSONArray keywords = jsonContent.getJSONArray("s45");
                String keyword = null;
                if (keywords != null) {
                    keyword = keywords.stream().map(Object::toString).collect(joining(","));
                    document8Entity.setKeyword(keyword);
                }
            }
            try {
                document8Mapper.insert(document8Entity);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


    public void sync2() {

    }
}
