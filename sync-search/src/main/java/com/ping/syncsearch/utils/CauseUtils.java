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
            map.values().stream().flatMap(Collection::stream).peek(c -> {
                List<Dict> dicts = map.get(c.getCode());
                if (dicts != null) {
                    c.setChild(dicts);
                }
            }).collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getCauseList(String code) {
        List<Dict> dicts = map.get(code);
        if (dicts == null || dicts.size() == 0) {
            return new HashSet<>();
        }
        Set<String> set = new HashSet<>();
        for (Dict dict1 : dicts) {
            set.add(dict1.getName());
            List<Dict> child = dict1.getChild();
            if (child != null && child.size() > 0) {
                for (Dict dict2 : child) {
                    set.add(dict2.getName());
                    List<Dict> child1 = dict2.getChild();
                    if (child1 != null && child1.size() > 0) {
                        for (Dict dict3 : child1) {
                            set.add(dict3.getName());
                            List<Dict> child2 = dict3.getChild();
                            if (child2 != null && child2.size() > 0) {
                                for (Dict dict4 : child2) {
                                    set.add(dict4.getName());
                                    List<Dict> child3 = dict4.getChild();
                                    if (child3 != null && child3.size() > 0) {
                                        for (Dict dict5 : child3) {
                                            set.add(dict5.getName());
                                            List<Dict> child4 = dict5.getChild();
                                            if (child4 != null && child4.size() > 0) {
                                                for (Dict dict6 : child4) {
                                                    set.add(dict6.getName());
                                                }
                                            }

                                        }

                                    }
                                }
                            }
                        }
                    }

                }
            }

        }
        set.add(dict.get(code));
        return set;
    }
}
