package com.ping.syncpaser;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.ping.syncparse.SyncParseApplication;
import com.ping.syncparse.service.*;
import com.ping.syncparse.service.borrow.BorrowService;
import com.ping.syncparse.service.contract.ContractResultMapper;
import com.ping.syncparse.service.contract.ContractService;
import com.ping.syncparse.service.contract.ContractTempMapper;
import com.ping.syncparse.service.contract.ContractTempVo;
import com.ping.syncparse.service.criminal.CriminalService;
import com.ping.syncparse.service.word.WordService;
import com.ping.syncparse.service.work.WorkService;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

@SpringBootTest(classes = SyncParseApplication.class)
@RunWith(SpringRunner.class)
class SyncPaserApplicationTests {

    @Autowired
    private ExportMsService msService;

    @Autowired
    private ExportTempService exportTempService;

    @Autowired
    private ExportXsService xsService;

    @Autowired
    private ParsePartyService parsePartyService;

    @Autowired
    private ParsePartyTempService parsePartyTempService;

    @Autowired
    private InternetFraudMapper tempMapper;

    @Autowired
    private TempService tempService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private ParsePartyEasyService parsePartyEasyService;

    @Autowired
    private ExportEasyService exportEasyService;


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
        parsePartyTempService.parse();
    }

    @Test
    public void test71() {
        exportService.export();
    }

    @Test
    public void export() {
        exportTempService.export();
    }

    @Test
    public void parse() {
        parsePartyEasyService.parse();
    }

    @Test
    public void export1() {
        exportEasyService.export();
    }

    @Test
    public void testConvert() {
        tempService.convert();
    }

    @Test
    public void testpc() throws Exception {
        List<DocumentTargetEntity> entities = tempMapper.findtargetList(0, 100, null);
        for (DocumentTargetEntity entity : entities) {
            String htmlContent = entity.getString("htmlContent");
            //   int year = DateUtil.year(entity.getDate("refereeDate"));
            //String temp = year + "\\" + "滥用职权罪";
            //  String temp = year + "\\" + "虐待被监管人罪";
            //  String temp = year + "\\" + "私放在押人员罪";
            //  String temp = year + "\\" + "玩忽职守罪";
            // String temp = year + "\\" + "刑讯逼供罪";
            // String temp = year + "\\" + "徇私舞弊减刑、假释、暂予监外执行罪";
            String temp = "2020";
            String name = entity.getString("name").replace(".", "");
            name = name.replace("*", "");
            name = name.replace(":", "");
            name = name.replace("?", "");
            name = Jsoup.parse(name).text();

            File file = new File("D:\\刑事案由\\" + temp);
            if (!file.exists()) {
                file.mkdirs();
            }
            String docPath = file.getPath() + "\\" + name + ".docx ";
            File f = new File(docPath);
            if (f.exists()) {
                docPath = file.getPath() + "\\" + name + "-" + RandomUtil.randomString(5) + ".docx";
            }
            System.out.println(docPath);
            htmlAsAltChunk2Docx(htmlContent, docPath);
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

    @Autowired
    private ExportResultService exportResultService;

    @Test
    public void test12() {
        exportResultService.export();
    }

    @Test
    public void testParse() {
        for (Term term : DicAnalysis.parse("户籍所在地广西壮族自治区玉林市容县")) {
            System.out.println(term.getNatureStr() + "=====" + term.getRealName());
        }
    }

    @Autowired
    private CriminalService criminalService;

    @Test
    public void criminaParse() {
        criminalService.parse();
    }

    @Autowired
    private BorrowService borrowService;

    @Test
    public void borrowService() {
        borrowService.parse();
    }

    @Autowired
    private ContractService contractService1;

    @Test
    public void ContractService() {
        String caseNo = "（2022）苏0922民初2258号";
        contractService1.parse(caseNo);
    }

    @Autowired
    private WorkService workService;

    @Test
    public void workService() {
        workService.parse();
    }


    @Autowired
    private ContractTempMapper contractTempMapper;

    public void tests() {
        String string = null;
        try {
            string = IOUtils.toString(new FileInputStream("D:\\金融合同纠纷解析意见反馈\\Sample.txt"), Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> ids = JSON.parseArray(string, String.class);
        Criteria criteria = Criteria.where("_id").in(ids);
        List<ContractTempVo> entities = contractTempMapper.findList(0, 100, criteria);
    }

}
