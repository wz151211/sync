package com.ping.syncparse.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ping.syncparse.entity.DocumentEntity;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.mapper.DocumentMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Author: W.Z
 * @Date: 2022/12/15 22:09
 */
@Service
public class ParsePartyService {
    @Autowired
    private DocumentMapper documentMapper;

    private AtomicInteger pageNum = new AtomicInteger(0);

    private String[] temp = {"汉族", "满族", "回族", "藏族", "苗族", "彝族", "壮族", "侗族", "瑶族", "白族", "傣族", "黎族", "佤族", "畲族", "水族", "土族", "蒙古族", "布依族", "土家族", "哈尼族", "傈僳族", "高山族", "拉祜族", "东乡族", "纳西族", "景颇族", "哈萨克族", "维吾尔族", "达斡尔族", "柯尔克孜族", "羌族", "怒族", "京族", "德昂族", "保安族", "裕固族", "仫佬族", "布朗族", "撒拉族", "毛南族", "仡佬族", "锡伯族", "阿昌族", "普米族", "朝鲜族", "赫哲族", "门巴族", "珞巴族", "独龙族", "基诺族", "塔吉克族", "俄罗斯族", "鄂温克族", "塔塔尔族", "鄂伦春族", "乌孜别克族"};

    private int pageSize = 1000;
    private Set<String> nations = new HashSet<>();

    {
        nations.addAll(Arrays.asList(temp));
    }

    public void parse() {

        List<DocumentEntity> entities = documentMapper.findList(pageNum.get(), pageSize);
        pageNum.getAndIncrement();
        for (DocumentEntity entity : entities) {
            if (StringUtils.hasLength(entity.getJsonContent())) {
                JSONObject object = JSON.parseObject(entity.getJsonContent());
                JSONArray array = object.getJSONArray("s17");
                if (array != null && array.size() > 0) {
                    if (StringUtils.hasLength(entity.getHtmlContent())) {
                        Document parse = Jsoup.parse(entity.getHtmlContent());
                        Elements elements = parse.select(".PDF_pox");
                        if (elements == null || elements.size() == 0) {
                            elements = parse.select("div");
                        }

                        for (Object o : array) {
                            for (int i = 0; i < elements.size(); i++) {
                                Element element = elements.get(i);
                                String text = element.ownText();
                                if (text.contains(o.toString())) {
                                    parseText(text, o.toString());
                                    break;
                                }
                            }
                        }
                    }

                } else {

                }

            }


        }

    }

    private PartyEntity parseText(String text, String name) {
        text = text.replace("，", ",");
        text = text.replace("。", ",");
        String[] split = text.split(",");
        PartyEntity party = new PartyEntity();
        party.setContent(text);
        for (String s : split) {
            if (s.contains(name)) {
                party.setName(name);
            }
            if (s.contains("男") || s.contains("女")) {
                party.setSex(s);
            }
            if (s.contains("年") && s.contains("月") && s.contains("日") && s.contains("生")) {
                party.setBirthday(s);
            }
            if (nations.contains(s)) {
                party.setNation(s);
            }
            if (s.contains("住")) {
                party.setAddress(s);
            }
            if (s.contains("工") || s.contains("农民") || s.contains("无业")) {
                party.setPost(s);
            }
            if (s.contains("身份证") || s.contains("身份号")) {
                party.setIdCard(s);
            }
            if (s.contains("文化") || s.contains("文盲")) {
                party.setEduLevel(s);
            }
        }

        System.out.println(JSON.toJSONString(party));
        return party;
    }
}
