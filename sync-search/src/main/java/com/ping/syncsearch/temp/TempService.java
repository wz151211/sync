package com.ping.syncsearch.temp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

/**
 * @Author: W.Z
 * @Date: 2023/11/30 21:28
 */
@Service
public class TempService {

    @Autowired
    private TempMapper tempMapper;

    Criteria criteria = Criteria.where("s8").is("民事案件").and("s6").is("01");

    public void findCount(String collectName) {
        tempMapper.findCount(collectName, criteria);

    }

}
