package com.ping.syncpaser;

import cn.hutool.core.convert.NumberChineseFormatter;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.metadata.csv.CsvSheet;
import com.alibaba.excel.support.ExcelTypeEnum;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.ping.syncparse.entity.PartyEntity;
import com.ping.syncparse.service.CaseVo;
import com.ping.syncparse.service.InternetFraudEntity;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.library.DicLibrary;
import org.ansj.recognition.arrimpl.UserDefineRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.ansj.util.MyStaticValue;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.util.ZipSecureFile;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.docx4j.openpackaging.exceptions.InvalidFormatException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;
import org.docx4j.openpackaging.parts.WordprocessingML.AltChunkType;
import org.docx4j.openpackaging.parts.WordprocessingML.MainDocumentPart;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: W.Z
 * @Date: 2022/12/16 10:49
 */
public class Test1 {

    @Test
    public void test1() {

        String str = "汉族、满族、回族、藏族、苗族、彝族、壮族、侗族、瑶族、白族、傣族、黎族、佤族、畲族、水族、土族、蒙古族、布依族、土家族、哈尼族、傈僳族、高山族、拉祜族、东乡族、纳西族、景颇族、哈萨克族、维吾尔族、达斡尔族、柯尔克孜族、羌族、怒族、京族、德昂族、保安族、裕固族、仫佬族、布朗族、撒拉族、毛南族、仡佬族、锡伯族、阿昌族、普米族、朝鲜族、赫哲族、门巴族、珞巴族、独龙族、基诺族、塔吉克族、俄罗斯族、鄂温克族、塔塔尔族、鄂伦春族、乌孜别克族";
        String[] split = str.split("、");
        System.out.println(split.length);
        System.out.println(JSON.toJSONString(split));


    }

    @Test
    public void test() {
        DateTime parse = DateUtil.parse("2014-05-25 16:00:00");
        boolean after = parse.isAfter(new Date());
        boolean before = parse.isBefore(new Date());
        System.out.println(after);
        System.out.println(before);
        System.out.println(DateUtil.offsetHour(parse, 16));
    }

    @Test
    public void test3() {
        String str = "abcdfga";
        int i = str.indexOf("a");
        int i1 = str.lastIndexOf("a");
        System.out.println(i);
        System.out.println(i1);

    }

    @Test
    public void testArea() throws IOException {
        String path = "E:\\project\\ping\\sync\\sync-parse\\src\\main\\resources\\dict\\area.txt";
        FileInputStream inputStream = new FileInputStream(path);
        String text = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        JSONArray array = JSON.parseArray(text);
        for (Object o : array) {
            System.out.println(o);
        }

    }

    @Test
    public void testMd5() {
        DateTime parse = DateUtil.parse("2016-05-23 00:00:00.000");
        String format = DateUtil.format(parse.toJdkDate(), DatePattern.NORM_DATETIME_PATTERN);
        System.out.println(format);
        String uid = "成都顺美国际贸易有限公司与大安市财政局行政诉讼一案一审行政判决书" + "（2016）吉0882行初1号" + format;
        String md5 = MD5.create().digestHex(uid);
        System.out.println(md5);

    }

    @Test
    public void testpc() throws Exception {
        String path = "D:\\Compressed\\北大法宝司法案例库批量下载20230303202439\\";
        File file = new File(path);
        for (File listFile : file.listFiles()) {
            List<String> lines = Files.readAllLines(listFile.toPath(), StandardCharsets.UTF_8);
            lines.remove(0);
            lines.remove(0);
            lines.remove(0);
            String docPath = path + lines.get(0) + ".docx";
            File docFile = new File(docPath);
            if (docFile.exists()) {
                docPath = path + lines.get(0) + "-" + RandomUtil.randomString(5) + ".docx";
            }
            StringBuilder sb = new StringBuilder();
            for (String line : lines) {
                sb.append("<div style='LINE-HEIGHT: 25pt; TEXT-INDENT: 30pt; MARGIN: 0.5pt 0cm;FONT-FAMILY: 宋体; FONT-SIZE: 15pt;'>").append(line).append("</div>");
            }
            Document parse = Jsoup.parse(sb.toString());
            htmlAsAltChunk2Docx(parse.html(), docPath);
        }
    }

    @Test
    public void testToWord() throws Exception {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors() * 2,
                30,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                new ThreadPoolExecutor.CallerRunsPolicy());
        String path = "G:\\案件数据7.3w";
        String target = "G:\\案件数据7.3w-word\\";
        File file = new File(path);
        for (File listFile : file.listFiles()) {
            String docPath = target + FilenameUtils.getBaseName(listFile.getName()) + ".docx";
            File docFile = new File(docPath);
            if (docFile.exists()) {
                docPath = path + FilenameUtils.getBaseName(listFile.getName()) + "-" + RandomUtil.randomString(5) + ".docx";
            }
            String content = IOUtils.toString(new FileInputStream(listFile), "GB2312");

            String finalDocPath = docPath;
            executor.execute(() -> {
                htmlAsAltChunk2Docx(content, finalDocPath);
            });

        }
    }


    @Test
    public void  test1111(){
        String content = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
                "\n" +
                "<HTML>\n" +
                "\n" +
                "<HEAD>\n" +
                "\n" +
                " <META HTTP-EQUIV=\"CONTENT-TYPE\" CONTENT=\"text/html; charset=UTF-8\">\n" +
                "\n" +
                " <TITLE></TITLE>\n" +
                "\n" +
                " <META NAME=\"GENERATOR\" CONTENT=\"OpenOffice 4.1.3  (Win32)\">\n" +
                "\n" +
                " <META NAME=\"AUTHOR\" CONTENT=\"NTKO\">\n" +
                "\n" +
                " <META NAME=\"CREATED\" CONTENT=\"20190422;15250000\">\n" +
                "\n" +
                " <META NAME=\"CHANGEDBY\" CONTENT=\"9080\">\n" +
                "\n" +
                " <META NAME=\"CHANGED\" CONTENT=\"20190422;16340000\">\n" +
                "\n" +
                " <STYLE TYPE=\"text/css\">\n" +
                "\n" +
                " <!--\n" +
                "\n" +
                "  @page { margin-right: 2.6cm; margin-top: 3.7cm; margin-bottom: 1.75cm }\n" +
                "\n" +
                "  P { margin-bottom: 0.21cm; direction: ltr; color: #000000; text-align: justify; widows: 0; orphans: 0 }\n" +
                "\n" +
                "  P.western { font-family: \"Times New Roman\", \"Times\", serif; font-size: 10pt; so-language: en-US }\n" +
                "\n" +
                "  P.cjk { font-family: \"宋体\", \"SimSun\"; font-size: 10pt; so-language: zh-CN }\n" +
                "\n" +
                "  P.ctl { font-family: \"Times New Roman\", \"Times\", serif; font-size: 12pt; so-language: ar-SA }\n" +
                "\n" +
                " -->\n" +
                "\n" +
                " </STYLE>\n" +
                "\n" +
                "</HEAD>\n" +
                "\n" +
                "<BODY LANG=\"zh-CN\" TEXT=\"#000000\" DIR=\"LTR\">\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=JUSTIFY STYLE=\"margin-bottom: 0cm; line-height: 1.41cm\">\n" +
                "\n" +
                "<FONT SIZE=6 STYLE=\"font-size: 22pt\">西  安  市  未  央  区  人  民 \n" +
                "\n" +
                "法  院  </FONT>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=CENTER STYLE=\"margin-bottom: 0cm; line-height: 1.41cm\">\n" +
                "\n" +
                "<FONT SIZE=6 STYLE=\"font-size: 26pt\">执 行 裁 定 书</FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-bottom: 0cm; line-height: 1.41cm\">\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">（</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">2019</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">）陕</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">0112</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">执</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">1813</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">号</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-bottom: 0cm; line-height: 1.41cm\">\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.13cm; margin-bottom: 0cm; widows: 2; orphans: 2\"><A NAME=\"Label_msg_1462\"></A><A NAME=\"Label_msg_1469\"></A>\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">申请执行人中国民生银行股份有限公司西安分行。住所地：西安市楼。统一社会信用代码</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">XXXXXXXXXXXXXX1970</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">。</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.13cm; margin-bottom: 0cm; widows: 2; orphans: 2\">\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">负责人薛文才，该分行行长。</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.13cm; margin-bottom: 0cm; widows: 2; orphans: 2\"><A NAME=\"Label_msg_1463\"></A><A NAME=\"Label_msg_1466\"></A>\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">被执行人姜辉，女，</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">1980</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">年</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">5</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">月</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">30</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">日出生，汉族，住西安市未央区，公民身份号码</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">XXXXXXXXXXXXXXXXXX</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">。</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.13cm; margin-bottom: 0cm; widows: 2; orphans: 2\"><A NAME=\"Label_msg_1464\"></A><A NAME=\"Label_msg_1467\"></A>\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">被执行人薛健华，女，</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">1972</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">年</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">4</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">月</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">16</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">日出生，汉族，住西安市新城区，公民身份号码</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">XXXXXXXXXXXXXXXXXX</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">。</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.13cm; margin-bottom: 0cm; widows: 2; orphans: 2\"><A NAME=\"Label_msg_1465\"></A><A NAME=\"Label_msg_1468\"></A>\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">被执行人刘红兵，男，</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">1971</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">年</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">11</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">月</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">26</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">日出生，汉族，住河南省许昌市魏都区，公民身份号码</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">XXXXXXXXXXXXXXXXXX</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">。</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.13cm; margin-bottom: 0cm; widows: 2; orphans: 2\">\n" +
                "\n" +
                "   \n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">本院在执行申请执行人</FONT></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">中国民生银行股份有限公司西安分行</FONT></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">与被执行人</FONT></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">姜辉、薛建华、刘红兵公证文书一案中，申请执行人中国民生银行股份有限公司西安分行</FONT></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">依据已经发生法律效力的陕西省西安市公证处</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">(2017)</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">西证经字第</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">3924</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">号公证书和（</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">2018</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">）西证执字第</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">233</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">号执行证书于</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">2019</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">年</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">2</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">月</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">3</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">日向本院申请执行，本院依法受理。执行中，</FONT></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">申请执行人中国民生银行股份有限公司西安分行于</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">2019</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">年</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">4</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">月</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">9</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">日</FONT></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">向本院申请撤回执行申请，本院予以准许。依照《中华人民共和国民事诉讼法》第一百五十四条第（八）项、第二百五十七条（六）项之规定，裁定如下：</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"margin-right: 1.11cm; text-indent: 1.11cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">终结本院（</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">2019</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">）陕</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">0112</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">执</FONT></FONT><FONT FACE=\"Times New Roman, Times, serif\"><SPAN LANG=\"en-US\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">1813</FONT></FONT></SPAN></FONT><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">号案件的执行。</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.11cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">本裁定送达后立即生效。</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.11cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"text-indent: 1.11cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"margin-right: 1.27cm; text-indent: 1.11cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-right: 0.16cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">审\n" +
                "\n" +
                " 判  长    康晓纲</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-right: 0.16cm; text-indent: 1.13cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "       <FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">审\n" +
                "\n" +
                " 判  员    贺  诚</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-right: 0.16cm; text-indent: 1.13cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">审\n" +
                "\n" +
                " 判  员    王  昆</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-right: 0.16cm; text-indent: 1.13cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-right: 0.16cm; text-indent: 1.13cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"text-indent: 1.08cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">二&#12295;一九年四月十七日</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"text-indent: 1.08cm; margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "<BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" ALIGN=RIGHT STYLE=\"margin-bottom: 0cm; line-height: 1.06cm\">\n" +
                "\n" +
                "           <FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">书\n" +
                "\n" +
                " 记  员    关海霞</FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"margin-bottom: 0cm; line-height: 1.06cm\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">打印人：关海霞\n" +
                "\n" +
                "</FONT></FONT><FONT FACE=\"仿宋, 宋体, monospace\">  </FONT><FONT COLOR=\"#000000\"><FONT FACE=\"仿宋, 宋体, monospace\"><FONT SIZE=4 STYLE=\"font-size: 16pt\">校对人：刘启&#21894;\n" +
                "\n" +
                "  送达时间：    年  月  日</FONT></FONT></FONT></P>\n" +
                "\n" +
                "<P CLASS=\"cjk\" STYLE=\"margin-bottom: 0cm\"><BR>\n" +
                "\n" +
                "</P>\n" +
                "\n" +
                "<DIV TYPE=FOOTER>\n" +
                "\n" +
                " <P ALIGN=RIGHT STYLE=\"margin-top: 1.65cm; margin-bottom: 0cm\"><SDFIELD TYPE=PAGE SUBTYPE=RANDOM FORMAT=PAGE>2</SDFIELD></P>\n" +
                "\n" +
                " <P ALIGN=LEFT STYLE=\"margin-bottom: 0cm\"><BR>\n" +
                "\n" +
                " </P>\n" +
                "\n" +
                "</DIV>\n" +
                "\n" +
                "</BODY>\n" +
                "\n" +
                "</HTML>";
      String path = "/Users/monkey/Desktop/test/未命名.docx";
      htmlAsAltChunk2Docx(content,path);
    }

    public void htmlAsAltChunk2Docx(String html, String docxPath) {

        try {
            WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.createPackage();

            MainDocumentPart mdp = wordMLPackage.getMainDocumentPart();
            //   wordMLPackage.setFontMapper(IFontHandler.getFontMapper());
            // Add the Html altChunk
            //  String html = sb.toString();
            if (StringUtils.isEmpty(html)) {
                mdp.addAltChunk(AltChunkType.Html, "<html><center>不公开理由：人民法院认为不宜在互联网公布的其他情形</center></html>".getBytes(StandardCharsets.UTF_8));

            } else {
                if (html.contains("charset=GBK")) {
                    html = html.replace("charset=GBK", "charset=UTF-8");
                }

                if (html.contains("charset=GB2312")) {
                    html = html.replace("charset=GB2312", "charset=UTF-8");
                }

                mdp.addAltChunk(AltChunkType.Html, html.getBytes());

            }

            // Round trip
            WordprocessingMLPackage pkgOut = mdp.convertAltChunks();

            pkgOut.save(new File(docxPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test02() {
        Result parse = ToAnalysis.parse("用于金某接收贪污款项共计人民币380余万元");
        for (Term term : parse.getTerms()) {
            System.out.println(term.getRealName());
            for (int i = 0; i < 5; i++) {
                if (i == 2) {
                    System.out.println("--------------------------");
                    break;
                }
            }
        }
    }

    @Test
    public void test03() throws Exception {
        org.apache.poi.util.IOUtils.setByteArrayMaxOverride(100000000 * 1000);
        ZipSecureFile.setMinInflateRatio(0.000001);
        String path = "E:\\导出\\刑事案件11.xlsx";
        FileInputStream inputStream = new FileInputStream(path);
        SXSSFWorkbook sxssfWorkbook = null;
        Sheet sheet = null;
        Sheet partySheet = null;
        Sheet partySheet2 = null;
        XSSFWorkbook wb = null;

        wb = new XSSFWorkbook(inputStream);
        sxssfWorkbook = new SXSSFWorkbook(wb);
        sheet = sxssfWorkbook.getSheetAt(0);
        Row row0 = sheet.getRow(0);
        Row row = sheet.getRow(10);


        try (OPCPackage opcPackage = OPCPackage.open(new File(path))) {
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            XSSFSheet sheetAt = workbook.getSheetAt(0);
            int lastRowNum = sheetAt.getLastRowNum();

            sxssfWorkbook = new SXSSFWorkbook(workbook, 10000);
            Sheet sheet1 = sxssfWorkbook.getSheetAt(0);
            int lastRowNum1 = sxssfWorkbook.getXSSFWorkbook().getSheetAt(0).getLastRowNum();
            int rowNum = sheet1.getLastRowNum() + 1;
            String sheetName = sheet1.getSheetName();


        }
    }

    @Test
    public void test04() {
        String realName = "3.2018年";
        if (realName.contains(".")) {
            int index1 = realName.indexOf(".");
            realName = realName.substring(index1 + 1);
            System.out.println(realName);
        }


    }

    @Test
    public void test5() {

        String path = "E:\\导出\\刑事案件11.csv";

        try (ExcelWriter excelWriter = EasyExcel.write(path, CaseVo.class).excelType(ExcelTypeEnum.CSV).writeExcelOnException(true).build()) {
            List<CaseVo> data = new ArrayList<>();
            WriteSheet partSheet = EasyExcel.writerSheet(1, "当事人信息").build();

            excelWriter.write(data, partSheet);
        }
    }

    @Test
    public void test6() {
        Pattern AMOUNT_PATTERN =
                Pattern.compile("^(0|[1-9]\\d{0,11})\\.(\\d\\d)$"); // 不考虑分隔符的正确性
        String amount = "叁佰叁拾陆";
        Matcher matcher = AMOUNT_PATTERN.matcher(amount);
        System.out.println(matcher.find());
        int i = NumberChineseFormatter.chineseToNumber(amount);
        System.out.println(i);
    }

    @Test
    public void test7() {
        MyStaticValue.ENV.put(DicLibrary.DEFAULT, "library/default.dic");
        DicLibrary.insertOrCreate(DicLibrary.DEFAULT, "4日", "t", 1000);
        for (Term term : NlpAnalysis.parse("约定逾期利息月利率一分二厘")) {
            System.out.println(term.getNatureStr() + "=====" + term.getRealName());
        }
    }

    @Test
    public void test11() {
        List<CaseVo> list = new ArrayList<>();
        list.add(new CaseVo());
        list.add(new CaseVo());
        list.add(new CaseVo());
        list.add(new CaseVo());
        list.add(new CaseVo());
        List<CaseVo> collect = list.stream().collect(Collectors.toList());
        for (CaseVo caseVo : collect) {
            caseVo.setName("1");
        }
        System.out.println(collect.size());
    }

    @Test
    public void tset12() {
        String s1 = "二审案件受理费2087元";
        int start = s1.indexOf("受理费");
        if (start == -1) {
            start = s1.indexOf("诉讼费");
        }
        int end = s1.indexOf("元");
        if (end > start + 3) {
            String s2 = s1.substring(start + 3, end);
            System.out.println(s2);
        }
    }

    @Test
    public void test13() {
        String temp = "2021年8月份原告与被告智某1通过手机抖音视频相识";
        int start = temp.indexOf("经");
        if (start == -1) {
            start = temp.indexOf("通过");
        }
        int end = temp.indexOf("相识");
        if (end == -1) {
            end = temp.indexOf("认识");
        }
        if (end > start) {
            System.out.println(temp.substring(start, end + 2));
        }
        System.out.println(DateUtil.date().hour(true));
    }

    @Test
    public void testHtml() {
        System.out.println(NumberChineseFormatter.chineseToNumber("贰拾玖万捌仟零肆拾陆"));
    }

    @Test
    public void test111() {
        String startDate = "2012年5月02";
        if (startDate.contains("年") && startDate.contains("月") && !startDate.contains("日")) {
            int index = startDate.indexOf("月");
            if (index > -1) {
                startDate = startDate.substring(0, index + 1);
                startDate = startDate + "01日";
            }
            System.out.println(startDate);
            DateTime dateTime = DateUtil.parse(startDate);
            System.out.println(dateTime);
        }
    }

    @Test
    public void test22() {
        System.out.println(NumberChineseFormatter.chineseToNumber("十二"));
    }

    @Test
    public void test33() {
        System.out.println(NumberChineseFormatter.chineseToNumber("十二"));
    }

    @Test
    public void test44() {
        PartyEntity party = new PartyEntity();
        party.setName("车献梁");
        party.setType("被告");
        boolean bg = false;
        if ("被告".equals(party.getType())
                && org.springframework.util.StringUtils.hasLength(party.getName())
                && ((!party.getName().contains("公司")
                && !party.getName().contains("银行")
                && !party.getName().contains("信用合作联社")
                && !party.getName().contains("信用社")
                && !party.getName().contains("工作室")
                && !party.getName().contains("批发商")
                && !party.getName().contains("超市")
                && !party.getName().contains("百货店")
                && party.getName().length() <= 5)
                || (party.getName().contains("厂") && party.getName().length() <= 3))) {
            bg = true;
            System.out.println("-------------");
        }
    }

    @Test
    public void test45() {
        String text = "身份证号码";
        if (text.contains("身份证号")) {
            int index = text.indexOf("身份证号");
            String temp = text.substring(index + 4);
            temp = temp.replace("：", "");
            temp = temp.replace(":", "");
            temp = temp.replace("）", "");
            temp = temp.replace(")", "");
            temp = temp.replace("。", "");
            temp = temp.replace(".", "");
            temp = temp.replace("码", "");
            System.out.println(temp);

        }
    }

    @Test
    public void test46() {
        String temp = "35032219*******分手大师";
        for (byte aByte : temp.getBytes()) {
            System.out.println(aByte);
        }
        System.out.println("--------------");
        for (char c : temp.toCharArray()) {
            System.out.println(c);
        }
        System.out.println("--------------");

        for (String s : temp.split("")) {
            System.out.println(s);
        }
    }

    @Test
    public void test47() throws IOException {
        File file = new File("E:\\案件包\\反馈包");
        for (File listFile : file.listFiles()) {
            String path = listFile.getParent();
            String name = listFile.getName();
            File to = new File(path + "\\~" + name);
            listFile.renameTo(to);
        }

    }

    @Test
    public void test48() {
        int count = 0;
        File file = new File("G:\\word\\");
        for (File listFile : file.listFiles()) {
            boolean file1 = listFile.isFile();
            if (!file1) {
                String[] list = listFile.list();
                count += list.length;
            }

        }
        System.out.println(count);

    }
}
