package com.ping.syncsearch.temp;

import com.ping.syncsearch.utils.CauseUtils;
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


/*       Criteria criteria = Criteria.where("qwContent").regex("知假买假").orOperator(
               Criteria.where("qwContent").regex("职业打假"),
               Criteria.where("qwContent").regex("职业索赔"),
               Criteria.where("qwContent").regex("惩罚性赔偿"),
               Criteria.where("qwContent").regex("消费者"),
               Criteria.where("qwContent").regex("假一赔"),
               Criteria.where("qwContent").regex("食品安全法"),
               Criteria.where("qwContent").regex("药品管理法")
       );*/
//Criteria criteria = Criteria.where("qwContent").regex("商业银行").orOperator(Criteria.where("qwContent").regex("适当性"),Criteria.where("qwContent").regex("理财"));

    public void findCount(String collectName) {
        Criteria criteria = new Criteria();
    /*    criteria.orOperator(
                Criteria.where("qwContent").regex("知假买假"),
                Criteria.where("qwContent").regex("职业打假"),
                Criteria.where("qwContent").regex("职业索赔"),
                Criteria.where("qwContent").regex("惩罚性赔偿"),
                Criteria.where("qwContent").regex("消费者"),
                Criteria.where("qwContent").regex("假一赔"),
                Criteria.where("qwContent").regex("食品安全法"),
                Criteria.where("qwContent").regex("药品管理法"));*/
        criteria.and("s8").is("民事案件").and("s6").is("01").andOperator(Criteria.where("qwContent").regex("商业银行"), Criteria.where("qwContent").regex("适当性"), Criteria.where("qwContent").regex("理财"));
        tempMapper.findCount(collectName, criteria);

    }

}
