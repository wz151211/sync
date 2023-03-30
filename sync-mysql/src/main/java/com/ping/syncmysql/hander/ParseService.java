package com.ping.syncmysql.hander;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@DS("dm1")
public class ParseService {
    @Autowired
    private ParseMapper parseMapper;

    public void save(ParseEntity entity) {
        parseMapper.insert(entity);
    }

    public void update(ParseEntity entity) {
        parseMapper.update(null, Wrappers.<ParseEntity>lambdaUpdate()
                .set(ParseEntity::getDocType, entity.getDocType())
                .set(ParseEntity::getCause, entity.getCause())
                .set(ParseEntity::getTrialProceedings, entity.getTrialProceedings())
                .eq(ParseEntity::getName, entity.getName()));

        parseMapper.update(null, Wrappers.<ParseEntity>lambdaUpdate()
                .set(ParseEntity::getCaseNo, entity.getCaseNo())
                .eq(ParseEntity::getName, entity.getName()));

        parseMapper.update(null, Wrappers.<ParseEntity>lambdaUpdate()
                .set(ParseEntity::getCourtName, entity.getCourtName())
                .eq(ParseEntity::getName, entity.getName()));

        parseMapper.update(null, Wrappers.<ParseEntity>lambdaUpdate()
                .set(ParseEntity::getRefereeDate, entity.getRefereeDate())
                .eq(ParseEntity::getName, entity.getName()));

    }
}
