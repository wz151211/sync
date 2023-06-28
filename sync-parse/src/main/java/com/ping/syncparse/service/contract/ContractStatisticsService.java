package com.ping.syncparse.service.contract;

import com.ping.syncparse.entity.PartyEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@Slf4j
public class ContractStatisticsService {
    @Autowired
    private ContractResultMapper resultMapper;
    @Autowired
    private ContractTempMapper tempMapper;
    private int pageSize = 10000;

    public void statistics() {
        List<ContractResultVo> entities = resultMapper.findList(5, pageSize, null);
        entities.parallelStream().forEach(entity -> {
            List<PartyEntity> parties = entity.getParty();
            if (parties != null && parties.size() > 0) {
                boolean bg = false;
                boolean yg = false;
                for (PartyEntity party : parties) {
                    if ("被告".equals(party.getType())
                            && StringUtils.hasLength(party.getName())
                            && !party.getName().contains("公司") && !party.getName().contains("银行")) {
                        bg = true;
                    }
                    if ("原告".equals(party.getType())
                            && StringUtils.hasLength(party.getName())
                            && (party.getName().contains("公司") || party.getName().contains("银行"))) {
                        yg = true;
                    }
                }
                if (yg && bg) {
                    log.info("{}", entity.getName());
                    ContractTempVo vo = new ContractTempVo();
                    BeanUtils.copyProperties(entity, vo);
                    tempMapper.insert(vo);
                    //  resultMapper.delete(entity);
                }
            }

        });
    }
}
