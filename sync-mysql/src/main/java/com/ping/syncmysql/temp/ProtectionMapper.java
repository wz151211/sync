package com.ping.syncmysql.temp;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper
public interface ProtectionMapper extends BaseMapper<ProtectionEntity> {

    default void update(ProtectionEntity entity) {
        update(null, Wrappers.<ProtectionEntity>lambdaUpdate()
                .set(StringUtils.hasLength(entity.getSymbol()), ProtectionEntity::getSymbol, entity.getSymbol())
                .set(StringUtils.hasLength(entity.getShortName()), ProtectionEntity::getShortName, entity.getShortName())
                .set(StringUtils.hasLength(entity.getFullName()), ProtectionEntity::getFullName, entity.getFullName())
                .set(StringUtils.hasLength(entity.getRelatedParty()), ProtectionEntity::getRelatedParty, entity.getRelatedParty())
                .eq(ProtectionEntity::getGid, entity.getGid()));
    }

    default void updateRelated(ProtectionEntity entity) {
        update(null, Wrappers.<ProtectionEntity>lambdaUpdate()
                .set(ProtectionEntity::getSymbol, entity.getSymbol())
                .set(ProtectionEntity::getRelatedParty, entity.getRelatedParty())
                .eq(ProtectionEntity::getGid, entity.getGid()));
    }

    default List<ProtectionEntity> get(String name) {
        return selectList(Wrappers.<ProtectionEntity>lambdaQuery().likeRight(ProtectionEntity::getPunishmentObject, name));
    }

    default List<ProtectionEntity> getContains(String name) {
        return selectList(Wrappers.<ProtectionEntity>lambdaQuery().like(ProtectionEntity::getPunishmentObject, name));
    }

}
