package com.ping.syncpaser;

import com.alibaba.fastjson.JSON;
import org.junit.jupiter.api.Test;

/**
 * @Author: W.Z
 * @Date: 2022/12/16 10:49
 */
public class Test1 {

    @Test
    public void  test1(){

        String str = "汉族、满族、回族、藏族、苗族、彝族、壮族、侗族、瑶族、白族、傣族、黎族、佤族、畲族、水族、土族、蒙古族、布依族、土家族、哈尼族、傈僳族、高山族、拉祜族、东乡族、纳西族、景颇族、哈萨克族、维吾尔族、达斡尔族、柯尔克孜族、羌族、怒族、京族、德昂族、保安族、裕固族、仫佬族、布朗族、撒拉族、毛南族、仡佬族、锡伯族、阿昌族、普米族、朝鲜族、赫哲族、门巴族、珞巴族、独龙族、基诺族、塔吉克族、俄罗斯族、鄂温克族、塔塔尔族、鄂伦春族、乌孜别克族";
        String[] split = str.split("、");
        System.out.println(split.length);
        System.out.println(JSON.toJSONString(split));


    }

}
