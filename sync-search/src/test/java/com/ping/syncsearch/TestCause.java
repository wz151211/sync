package com.ping.syncsearch;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.ping.syncsearch.utils.CauseUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class TestCause {

    @Test
    public void test1() {
        Set<String> causeList = CauseUtils.getCauseList("9299");
        System.out.println(JSON.toJSONString(causeList));


    }

    @Test
    public void test2() {
        try {
            File file = new File("C:\\Users\\ggdn1\\Desktop\\index.html");
            Document parse = Jsoup.parse(file);
            List<Node> nodes = parse.childNodes();

            Elements children = parse.children();
            Elements allElements = parse.getAllElements();
            for (Element allElement : allElements) {
                String name = allElement.tagName();
                if(name.equals("html") || name.equals("body") ||name.contains("root")){
                    continue;
                }
                System.out.println(allElement.text());
            }
            System.out.println(allElements.size());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
