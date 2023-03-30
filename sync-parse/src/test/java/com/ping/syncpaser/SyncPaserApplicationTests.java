package com.ping.syncpaser;

import cn.hutool.core.util.RandomUtil;
import com.ping.syncparse.SyncParseApplication;
import com.ping.syncparse.service.*;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest(classes = SyncParseApplication.class)
@RunWith(SpringRunner.class)
class SyncPaserApplicationTests {

    @Autowired
    private ExportMsService msService;

    @Autowired
    private ExportXsService xsService;

    @Autowired
    private ParsePartyService parsePartyService;

    @Autowired
    private InternetFraudMapper tempMapper;

    @Autowired
    private TempService tempService;

    @Autowired
    private ExportService exportService;


    @Test
    void contextLoads() {
    }

    @Test
    public void test1() {
        msService.export();
    }


    @Test
    public void test2() {
        xsService.export();
    }

    @Test
    public void test7() {
        parsePartyService.parse();
    }

    @Test
    public void test71() {
        exportService.export();
    }

    @Test
    public void testConvert() {
        tempService.convert();
    }

    @Test
    public void testpc() throws Exception {
        List<InternetFraudEntity> entities = tempMapper.findList(0, 0, null);
        for (InternetFraudEntity entity : entities) {
            String htmlContent = entity.getHtmlContent();
            String temp = "猥亵儿童罪";
            entity.setName(entity.getName().replace(".", ""));
            entity.setName(entity.getName().replace("*", ""));
            String docPath = "D:\\刑事案由\\" + temp + "\\" + entity.getName() + ".docx";
            File file = new File(docPath);
            if (file.exists()) {
                docPath = "D:\\刑事案由\\" + temp + "\\" + entity.getName() + "-" + RandomUtil.randomString(5) + ".docx";
            }
            htmlAsAltChunk2Docx(htmlContent, docPath);
        }
    }

    public void htmlAsAltChunk2Docx(String html, String docxPath) throws Exception {

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
