package com.ping.syncmysqltomongo;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncmysqltomongo.mongo.mapper.AreaMongoMapper;
import com.ping.syncmysqltomongo.mysql.AreaMapper;
import com.ping.syncmysqltomongo.mysql.temp.AreaEntity;
import com.ping.syncmysqltomongo.temp.TempService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest(classes = SyncMysqlToMongoApplication.class)
@RunWith(SpringRunner.class)
class SyncMysqlToMongoApplicationTests {
    @Autowired
    private TempService tempService;

    @Test
    void contextLoads() {
        tempService.sync();
    }

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private AreaMongoMapper mapper;

    @Test
    public void testSync() {
        List<AreaEntity> areaEntities = areaMapper.selectList(Wrappers.lambdaQuery());
        for (AreaEntity entity : areaEntities) {
            com.ping.syncmysqltomongo.mongo.entity.AreaEntity areaEntity = new com.ping.syncmysqltomongo.mongo.entity.AreaEntity();
            BeanUtils.copyProperties(entity, areaEntity);
            mapper.insert(areaEntity);
        }
    }

}
