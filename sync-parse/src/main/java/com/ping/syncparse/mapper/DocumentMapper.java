package com.ping.syncparse.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ping.syncparse.entity.DocumentEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @Author: W.Z
 * @Date: 2022/8/21 22:42
 */
@Mapper
public interface DocumentMapper extends BaseMapper<DocumentEntity> {

    default List<DocumentEntity> findList(int pageNum, int pageSize) {
       return selectList(Wrappers.<DocumentEntity>lambdaQuery().last("limit " + pageNum * pageSize + "," + pageSize));
    }
}
