package com.ping.syncparse.service.word;

import cn.hutool.core.util.RandomUtil;
import com.ping.syncparse.service.DocumentTargetEntity;
import com.ping.syncparse.service.InternetFraudMapper;
import com.ping.syncparse.service.TempMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class WordService {
    @Autowired
    private InternetFraudMapper internetFraudMapper;
    private int pageSize = 6000;
    private AtomicInteger pageNum = new AtomicInteger(0);

    public void toWord() {
        log.info("当前页={}", pageNum.get());
        //  Criteria criteria = Criteria.where("s8").is("民事案件");
        List<DocumentTargetEntity> entities = internetFraudMapper.findtargetList(pageNum.get(), pageSize, null);
        for (DocumentTargetEntity entity : entities) {
            String htmlContent = entity.getString("htmlContent");
            //   int year = DateUtil.year(entity.getDate("refereeDate"));
            //String temp = year + "\\" + "滥用职权罪";
            //  String temp = year + "\\" + "虐待被监管人罪";
            //  String temp = year + "\\" + "私放在押人员罪";
            //  String temp = year + "\\" + "玩忽职守罪";
            // String temp = year + "\\" + "刑讯逼供罪";
            // String temp = year + "\\" + "徇私舞弊减刑、假释、暂予监外执行罪";
            String temp = "2021.06.01-2023.05.31样本";

            String name = entity.getString("name");
            String caseNo = entity.getString("caseNo");
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

            File file = new File("G:\\words\\" + temp);
            if (!file.exists()) {
                file.mkdirs();
            }
            String docPath = file.getPath() + "\\" + caseNo + "-" + name + ".docx ";
            File f = new File(docPath);
            if (f.exists()) {
                docPath = file.getPath() + "\\" + caseNo + "-" + name + "-" + RandomUtil.randomString(5) + ".docx";
            }
            log.info("文件:{}", docPath);
            try {
                htmlAsAltChunk2Docx(htmlContent, docPath);
                //  internetFraudMapper.delete(entity.getString("_id"));
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
        if (StringUtils.isEmpty(html)) {
            mdp.addAltChunk(AltChunkType.Html, "<html><center>不公开理由：人民法院认为不宜在互联网公布的其他情形</center></html>".getBytes(StandardCharsets.UTF_8));

        } else {
            mdp.addAltChunk(AltChunkType.Html, html.getBytes());

        }

        // Round trip
        WordprocessingMLPackage pkgOut = mdp.convertAltChunks();

        pkgOut.save(new File(docxPath));
    }
}
