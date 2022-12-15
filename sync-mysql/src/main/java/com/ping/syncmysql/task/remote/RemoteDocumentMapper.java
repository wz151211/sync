package com.ping.syncmysql.task.remote;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ping.syncmysql.task.local.DocumentEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Mapper
public interface RemoteDocumentMapper extends BaseMapper<DocumentEntity> {
}
