package com.ping.syncmysqltomongo.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ping.syncmysqltomongo.mysql.temp.AreaEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AreaMapper extends BaseMapper<AreaEntity> {

}
