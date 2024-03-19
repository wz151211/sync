package com.ping.syncparse.service.word;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ping.syncparse.common.Dict;
import com.ping.syncparse.service.DocumentTargetEntity;
import com.ping.syncparse.service.InternetFraudMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class WordService {
    @Autowired
    private InternetFraudMapper internetFraudMapper;
    private int pageSize = 10000;
    private AtomicInteger pageNum = new AtomicInteger(0);

    private List<Dict> docTypes = new ArrayList<>();
    private Map<String, String> docTypeMap = new HashMap<>();

    {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:dict/*.txt");
            for (Resource resource : resources) {
                if ("docType.txt".equals(resource.getFilename())) {
                    String text = IOUtils.toString(resource.getURI(), StandardCharsets.UTF_8);
                    docTypes.addAll(JSON.parseArray(text, Dict.class));
                    for (Dict docType : docTypes) {
                        docTypeMap.put(docType.getCode().trim(), docType.getName());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void toWord() {
        log.info("当前页={}", 5);
        //  Criteria criteria = Criteria.where("s8").is("民事案件");
        List<DocumentTargetEntity> entities = internetFraudMapper.findtargetList(5, pageSize, null);
        pageNum.getAndIncrement();
        for (DocumentTargetEntity entity : entities) {
            String htmlContent = entity.getString("qwContent");
            int year = 0;
            int month = 0;
            try {
                year = DateUtil.year(entity.getDate("s31"));
                month = DateUtil.month(entity.getDate("s31")) + 1;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            String temp = entity.getString("s8");

            String name = entity.getString("s1");
            if (StringUtils.isNotEmpty(name)) {
                name = Jsoup.parse(name).text();
                name = name.replace(".", "");
                name = name.replace("*", "");
                name = name.replace(":", "");
                name = name.replace("?", "");
                name = name.replace("\\", "");
                name = name.replace("/", "");
                name = name.replace(">", "");
                name = name.replace("<", "");
                name = name.replace("＇", "");
                name = name.replace("#", "");
                name = name.replace("p", "");
                name = name.replace("@", "");
                name = name.replace(";", "");
            } else {
                name = "";
            }
            if (StringUtils.isEmpty(temp)) {
                temp = "其他类型";
            }
            if (temp.equals("民事案件") || temp.equals("执行案件")) {
                temp = temp + File.separator + year + File.separator + month;
                JSONArray cause = entity.getJSONArray("s11");
                if (cause != null && cause.size() > 0) {
                    temp += File.separator + Optional.ofNullable(cause.getString(0)).orElse("其他");
                } else {
                    temp += File.separator + "其他";
                }
                String docType = entity.getString("s6");
                if (StringUtils.isNotEmpty(docType)) {
                    temp += File.separator + Optional.ofNullable(docTypeMap.get(docType)).orElse("其他");
                }
            }
            File file = new File("D:\\word" + File.separator + year + File.separator + temp);
            if (!file.exists()) {
                file.mkdirs();
            }
            String docPath = file.getPath() + File.separator + name + ".docx ";
            File f = new File(docPath);
            if (f.exists()) {
                docPath = file.getPath() + File.separator + name + "-" + RandomUtil.randomString(5) + ".docx";
            }
            log.info("文件:{}", docPath);
            try {
                htmlAsAltChunk2Docx(htmlContent, docPath);
                internetFraudMapper.delete(entity.getString("_id"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void htmlAsAltChunk2Docx(String html, String docxPath) throws Exception {
        if (!docxPath.contains("docx")) {
            System.out.println(docxPath);
        }
        WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();
        MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
        //   wordMLPackage.setFontMapper(IFontHandler.getFontMapper());
        // Add the Html altChunk
        //  String html = sb.toString();
        if (StringUtils.isNotEmpty(html)) {
            if (html.contains("charset=GBK")) {
                html = html.replace("charset=GBK", "charset=UTF-8");
            }
            if (html.contains("charset=GB2312")) {
                html = html.replace("charset=GB2312", "charset=UTF-8");
            }
        }

        if (StringUtils.isEmpty(html)) {
            mdp.addAltChunk(AltChunkType.Html, "<html><center>不公开理由：人民法院认为不宜在互联网公布的其他情形</center></html>".getBytes(StandardCharsets.UTF_8));
        } else {
            mdp.addAltChunk(AltChunkType.Html, html.getBytes(StandardCharsets.UTF_8));
        }

        // Round trip
        WordprocessingMLPackage pkgOut = mdp.convertAltChunks();

        pkgOut.save(new File(docxPath));
    }
}
