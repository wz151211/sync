package com.ping.syncmysqltomongo.mysql.temp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ping.syncmysqltomongo.mongo.temp.PartyEntity;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface PartyMapper extends BaseMapper<PartyEntity> {


}
