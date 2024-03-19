package com.ping.syncsearch.task;

import com.ping.syncsearch.service.QueryService;
import com.ping.syncsearch.utils.CauseUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

@Component
@Async
@Slf4j
public class Task1 {
    @Autowired
    private QueryService queryService;

    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("刑讯逼供");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("虐待被监管人");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("滥用职权");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("玩忽职守");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("执行判决、裁定失职");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("执行判决、裁定滥用职权");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("私放在押人员");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s11").is("失职致使在押人员脱逃");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("拐骗儿童");
    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s9").in("民事一审", "0301").and("s6").is("01").and("s11").is("经济补偿金纠纷");

    //   Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("非国家工作人员受贿").and("s31").gte("2014-01-01");
    //  Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("职务侵占").and("s31").gte("2014-01-01");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("挪用资金").and("s31").gte("2014-01-01");

    // Criteria criteria = Criteria.where("s7").regex("清");
    //  Criteria criteria = Criteria.where("s8").is("刑事案件").and("fyTree").is("上海市");
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("qwContent").regex("身份证号");

    Pattern pattern1 = Pattern.compile("^((?!高级).)*$", Pattern.CASE_INSENSITIVE);

    Pattern pattern2 = Pattern.compile("^((?!中级).)*$", Pattern.CASE_INSENSITIVE);


    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s6").is("01").and("qwContent").regex("商标权纠纷").andOperator(Criteria.where("s2").is(pattern1), Criteria.where("s2").is(pattern2));
    // Criteria criteria = Criteria.where("s8").is("执行案件").and("fyTree").is("佛山市").and("qwContent").regex("拍卖");
    // Criteria criteria = Criteria.where("fyTree").is("佛山市").and("s9").in("民事一审", "0301").and("s6").is("03");
    // Criteria criteria = Criteria.where("s8").is("民事案件").and("qwContent").regex("彩礼");

    //Criteria criteria = Criteria.where("s11").is("证券虚假陈述责任纠纷");
    // Criteria criteria = Criteria.where("qwContent").regex("证券虚假陈述责任纠纷");
    //Criteria criteria = Criteria.where("s8").is("刑事案件").orOperator(Criteria.where("s1").regex("公司"), Criteria.where("s1").regex("集团"));
    // Criteria criteria = Criteria.where("qwContent").regex("滋事").andOperator(Criteria.where("qwContent").regex("网络"));
    // Criteria criteria = Criteria.where("s9").in("刑事一审", "0201").and("s6").is("01").and("s1").regex("盗窃").and("fyTree").is("东莞市");
    // Criteria criteria = Criteria.where("s9").in("民事一审", "0301").and("s6").is("01").and("s11").is("离婚纠纷");
/*    private List<String> caseNo = new ArrayList<>();

    {
        File file = new File("E:\\case.txt");
        try {
            List<String> list = Files.readAllLines(file.toPath());
            for (String s : list) {
                s.trim();
                s = s.replace("(", "（");
                s = s.replace(")", "）");
                caseNo.add(s);
            }
            System.out.println(caseNo.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    // Criteria criteria = Criteria.where("s7").in(caseNo);
    // Criteria criteria = Criteria.where("s8").is("行政案件").and("qwContent").regex("入赘").andOperator(Criteria.where("qwContent").regex("土地"));
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("qwContent").regex("职务犯罪");
    // Criteria criteria = Criteria.where("s11").is("民间借贷纠纷").and("s6").is("01").and("s8").is("民事案件");
    //  Criteria criteria = Criteria.where("s6").is("01").and("s8").is("民事案件");

    //   Criteria criteria = Criteria.where("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("强奸");


    List<String> city = new ArrayList<>();

    {
        city.add("杭州市");
        city.add("成都市");
        city.add("苏州市");
        city.add("南京市");
        city.add("重庆市");
        city.add("武汉市");
        city.add("天津市");
        city.add("长沙市");
        city.add("西安市");
        city.add("青岛市");
        city.add("郑州市");
        city.add("宁波市");
        city.add("佛山市");
        city.add("东莞市");
        city.add("合肥市");
        city.add("北京市");
        city.add("上海市");
        city.add("广州市");
        city.add("深圳市");
    }

    // Criteria criteria = Criteria.where("s11").is("离婚后财产纠纷")
    //         .and("s6").is("01")
    //         .and("fyTree").in(city);
//Criteria criteria = Criteria.where("s6").is("01").and("s9").in("民事一审", "0301").and("s11").in(CauseUtils.getCauseList("9177"s));

    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s6").is("01");
    // Criteria criteria = Criteria.where("s9").in("民事一审", "0301").and("s6").is("01").and("s11").is("离婚纠纷").and("s43").is("01").and("s25").regex("殴打");

    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s6").is("01").and("qwContent").regex("纯粹经济损失");
    // Criteria criteria = Criteria.where("qwContent").regex("公益诉讼");
    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s11").in(CauseUtils.getCauseList("9363"));
    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s11").in(CauseUtils.getCauseList("9047"));
    // Criteria criteria = Criteria.where("s11").is("侵害发明专利权纠纷");
    // Criteria criteria = Criteria.where("s11").is("侵害实用新型专利权纠纷");
    // Criteria criteria = Criteria.where("s8").is("民事案件");

    // Criteria criteria = Criteria.where("s8").is("民事案件").and("s11").is("离婚后财产纠纷").and("s6").is("01");
    // Criteria criteria = Criteria.where("s31").gte("2021-10-01");

    //    Criteria criteria = Criteria.where("s8").is("民事案件").and("s6").is("01").and("s11").in(CauseUtils.getCauseList("9299"));

    // Criteria criteria = Criteria.where("s8").is("民事案件").and("qwContent").regex("妇女").andOperator(Criteria.where("qwContent").regex("农村土地"));


    //  Criteria criteria = Criteria.where("s11").is("猥亵儿童").and("s6").is("01");
    //  Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("qwContent").regex("杀人");
    //Criteria criteria = Criteria.where("s6").is("01").and("s8").is("行政案件");
    // Criteria criteria = Criteria.where("s11").is("诈骗");
    //Criteria criteria = Criteria.where("s6").is("01").and("s8").is("民事案件");

    //Criteria criteria = Criteria.where("s11").is("职务侵占").and("s6").is("01");
    // Criteria criteria = Criteria.where("s11").is("挪用资金").and("s6").is("01");

    // Criteria criteria = Criteria.where("s8").is("刑事案件").andOperator(Criteria.where("qwContent").regex("故意杀人"), Criteria.where("qwContent").regex("杀害丈夫"));
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").in(CauseUtils.getCauseList("314"));
    // Criteria criteria = Criteria.where("s8").is("刑事案件").and("s9").in("刑事一审", "0201").and("s6").is("01").and("s11").is("抢夺");


    Criteria criteria = new Criteria();

    {
        criteria.orOperator(
                Criteria.where("qwContent").regex("知假买假"),
                Criteria.where("qwContent").regex("职业打假"),
                Criteria.where("qwContent").regex("职业索赔"),
                Criteria.where("qwContent").regex("惩罚性赔偿"),
                Criteria.where("qwContent").regex("消费者"),
                Criteria.where("qwContent").regex("假一赔"),
                Criteria.where("qwContent").regex("食品安全法"),
                Criteria.where("qwContent").regex("药品管理法"));
        Set<String> causeList = CauseUtils.getCauseList("9299");
        List<Criteria> criteriaList = new ArrayList<>();
        for (String cause : causeList) {
            criteriaList.add(Criteria.where("qwContent").regex(cause));
        }
        //  criteria.orOperator(criteriaList);

    }

    @Scheduled(initialDelay = 2 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2014() {
        try {
            queryService.sync2014(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }


  //  @Scheduled(initialDelay = 4 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void sync2015() {
        try {
            queryService.sync2015(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

   // @Scheduled(initialDelay = 6 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2016() {
        try {
            queryService.sync2016(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

  //  @Scheduled(initialDelay = 8 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2017() {
        try {
            queryService.sync2017(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

   // @Scheduled(initialDelay = 10 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2018() {
        try {
            queryService.sync2018(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

  //  @Scheduled(initialDelay = 12 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2019() {
        try {
            queryService.sync2019(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

  //  @Scheduled(initialDelay = 14 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2020() {
        try {
            queryService.sync2020(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

  //  @Scheduled(initialDelay = 16 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2021() {
        try {
            queryService.sync2021(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

   // @Scheduled(initialDelay = 18 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2022() {
        try {
            queryService.sync2022(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

 //   @Scheduled(initialDelay = 20 * 1000L, fixedRate = 1000 * 60 * 3L)
    public void save2023() {
        try {
            queryService.sync2023(criteria);
        } catch (Exception e) {
            log.error("", e);
        }
    }

}
