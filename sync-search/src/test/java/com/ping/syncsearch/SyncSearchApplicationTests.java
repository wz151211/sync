package com.ping.syncsearch;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncsearch.entity.BaseEntity;
import com.ping.syncsearch.entity.DocumentOtherEntity;
import com.ping.syncsearch.mapper.DocumentOtherMapper;
import com.ping.syncsearch.temp.TempEntity;
import com.ping.syncsearch.temp.TempMapper;
import com.ping.syncsearch.utils.BeanUtils;
import com.ping.syncsearch.utils.TripleDES;
import org.bson.json.JsonObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
class SyncSearchApplicationTests {

    @Autowired
    private TempMapper tempMapper;

    @Autowired
    private DocumentOtherMapper otherMapper;

    @Test
    void contextLoads() {
        for (TempEntity tempEntity : tempMapper.findList()) {
            JSONObject contet = tempEntity.getContet();
            if (contet == null) {
                contet = tempEntity.getContent();
            }
            String secretKey = contet.getString("secretKey");
            String result = contet.getString("result");
            String iv = DateUtil.format(new Date(), "yyyyMMdd");
            String decrypt = TripleDES.decrypt(secretKey, result, iv);
            JSONObject jsonObject = JSON.parseObject(decrypt);
            BaseEntity entity = BeanUtils.toEntity(jsonObject);
            DocumentOtherEntity other = BeanUtils.convert(entity, DocumentOtherEntity::new);
            otherMapper.insert(other);

        }

    }

}
