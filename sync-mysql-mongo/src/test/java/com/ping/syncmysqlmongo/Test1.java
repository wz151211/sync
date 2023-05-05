package com.ping.syncmysqlmongo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Test;

public class Test1 {

    @Test
    public void test1() {
        String str = "{\"s1#3!虈丽梅与张满达、王永旭民间借贷纠纷民事一审裁定书\",\"s2\":\"吉林省镇赉县人民法院\",\"s3\":\"774\",\"s5\":\"8497feb4d1da46bfa0d80ebe4ae35561\",\"s6\":\"02\",\"s7\":\"（2015）镇坦民初字第29号\",\"s8\":\"民事案件\",\"s9\":\"民事一审\",\"s31\":\"2015-01-30\",\"s41\":\"2015-12-08\",\"s22\":\"吉林省镇赉县人民法院\\n民事裁定书\\n（2015）镇坦民初字第29号\",\"s23\":\"本院在审理原告陈丽梅诉被告张满达、王永旭民间借贷纠纷一案中，原告陈丽梅于2015年1月30日向本院提出撤诉申请\",\"s26\":\"本院认为，原告陈丽梅的撤诉申请符合法律规定，依照《中华人民共和国民事诉讼法》第一百四十五条的规定，裁定如下\",\"s27\":\"准予原告陈丽梅撤回起诉。\\n案件受理费37元，由原告陈丽梅负担\",\"s28\":\"代理审判员李咏华\\n二〇一五年一月三十日\\n书记员张越\",\"s17\":[\"陈丽梅\",\"张满达\",\"王永旭\"],\"s45\":[],\"s11\":[\"民间借贷纠纷\"],\"wenshuAy\":[{\"key\":\"s14\",\"value\":\"9177\",\"text\":\"民间借贷纠纷\"}],\"s47\":[{\"tkx\":\"第一百四十五条第一款\",\"fgmc\":\"《中华人民共和国民事诉讼法（2013年）》\",\"fgid\":\"820574\"},{\"tkx\":\"第一百四十五条第二款\",\"fgmc\":\"《中华人民共和国民事诉讼法（2013年）》\",\"fgid\":\"820574\"}],\"relWenshu\":[],\"qwContent\":\"<!DOCTYPE HTML PUBLIC -//W3C//DTD HTML 4.0 Transitional//EN'><HTML><HEAD><TITLE></TITLE></HEAD><BODY><div style='TEXT-ALIGN: center; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 0cm; FONT-FAMILY: 黑体; FONT-SIZE: 18pt;'>吉林省镇赉县人民法院</div><div style='TEXT-ALIGN: center; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 0cm; FONT-FAMILY: 黑体; FONT-SIZE: 18pt;'>民 事 裁 定 书</div><div id='1'  style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 0cm;  FONT-FAMILY: 宋体;FONT-SIZE: 15pt; '>（2015）镇坦民初字第29号</div><div id='2'  style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>原告陈丽梅，蒙古族。</div><div style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>被告张满达，男，蒙古族。</div><div style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>被告王永旭，男，汉族。</div><div id='2'  style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>本院在审理原告陈丽梅诉被告张满达、王永旭民间借贷纠纷一案中，原告陈丽梅于2015年1月30日向本院提出撤诉申请。</div><div id='2'  style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>本院认为，原告陈丽梅的撤诉申请符合法律规定，依照《中华人民共和国民事诉讼法》第一百四十五条的规定，裁定如下：</div><div id='2'  style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>准予原告陈丽梅撤回起诉。</div><div id='2'  style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>案件受理费37元，由原告陈丽梅负担。</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 36pt 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>代理审判员　　李咏华</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 36pt 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>二〇一五年一月三十日</div><div style='TEXT-ALIGN: right; LINE-HEIGHT: 25pt; MARGIN: 0.5pt 36pt 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>书　记　员　　张　越</div><div style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'></div><div style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'></div><div style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'></div><div style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'></div></BODY></HTML>\",\"directory\":[\"1\",\"2\",\"2\",\"2\",\"2\",\"2\"],\"globalNet\":\"outer\",\"viewCount\":\"85\"}";

        int index = str.lastIndexOf("#3!");
        if (index > 0) {
            str = str.replace("#3!", "\":\"");
        } else {
            index = str.lastIndexOf("#");
            if (index > 0) {
                str = str.replace("#", "\"");

            }
        }
        JSONObject object = JSON.parseObject(str);
        System.out.println(object);
    }


    @Test
    public void test2() {
        String str = " 123#123#";
        System.out.println(str.indexOf("#"));
    }
}
