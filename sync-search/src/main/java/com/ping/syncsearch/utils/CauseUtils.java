package com.ping.syncsearch.utils;

import com.alibaba.fastjson.JSON;
import com.ping.syncsearch.vo.Dict;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

public class CauseUtils {

    private static List<Dict> causes = new ArrayList<>();
    private static Map<String, List<Dict>> map = new HashMap<>();
    private static Map<String, String> dict = new HashMap<>();


    static {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:dict/*.txt");
            for (Resource resource : resources) {
                if ("cause.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    causes.addAll(JSON.parseArray(text, Dict.class));
                    for (Dict cause : causes) {
                        dict.put(cause.getCode(), cause.getName());
                    }
                }
            }
            map = causes.parallelStream().collect(groupingBy(Dict::getPId));
            map.values()
                    .stream()
                    .flatMap(Collection::stream)
                    .peek(c -> {
                        List<Dict> dicts = map.get(c.getCode());
                        if (dicts != null) {
                            c.setChild(dicts);
                        }
                    })
                    .collect(toList());
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getCauseList(String code) {
        List<Dict> dicts = map.get(code);
        if (dicts == null || dicts.size() == 0) {
            return new HashSet<>();
        }
        Set<String> set = dicts.stream().flatMap(c -> c.getChild().stream()).map(Dict::getName).collect(toSet());
        set.add(dict.get(code));
        set.addAll(dicts.stream().map(Dict::getName).collect(toSet()));
        return set;
    }
}
