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
import com.ping.syncparse.service.CaseVo;
import com.ping.syncparse.service.InternetFraudEntity;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
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
    public void test4() {
        String html = "<div id='1'  style=\"line-height: 25pt; text-indent: 30pt; margin: 0.5pt 0cm; font-family: 宋体; font-size: 15pt\">\n" +
                " <p>\n" +
                "  1</p>\n" +
                " <p align=\"center\">\n" +
                "  湖南省宁乡市人民法院</p>\n" +
                " <p align=\"center\">\n" +
                "  刑 事 判 决 书</p>\n" +
                " <p align=\"right\">\n" +
                "  （2021）湘0182刑初840号</p>\n" +
                " <p>\n" +
                "  公诉机关湖南省宁乡市人民检察院。</p>\n" +
                " <p>\n" +
                "  被告人周志勇，男，1974年10月21日出生于湖南省宁乡市，高中文化，中共党员，经商，住湖南省宁乡市。2017年3月11日，因涉嫌诈骗罪被刑事拘留，2017年3月24日被取保候审。2019年7月23日经宁乡市人民检察院决定取保候审。2020年1月17日经本院决定取保候审。2020年5月6日经本院决定逮捕，次日执行逮捕。现羁押于宁乡市看守所。</p>\n" +
                " <p>\n" +
                "  辩护人谢扬驰，湖南联合天成律师事务所律师。</p>\n" +
                " <p>\n" +
                "  湖南省宁乡市人民检察院以宁检公诉刑诉［2020］18号起诉书指控被告人周志勇犯诈骗罪，于2020年1月16日向本院提起公诉。本院于2020年5月27日作出（2020）湘0124刑初51号刑事判决书。以被告人周志勇犯诈骗罪，判处有期徒刑十年二个月，并处罚金人民币三万元；责令被告人周志勇退赔被害人蔡某1经济损失人民币一百四十二万元。被告人周志勇对该判决不服，提起上诉。长沙市中级人民法院于2021年6月11日作出（2020）湘01刑终556号刑事裁定书，裁定发回本院重审。本院于2021年8月4日以（2021）湘0182刑初840号立案受理后，在审理过程中，湖南省宁乡市人民检察院于2021年10月15日因补充侦查需要，建议本院延期审理，本院决定延期审理一次。2021年11月12日，湖南省宁乡市人民检察院建议</p>\n" +
                " <p>\n" +
                "  2</p>\n" +
                " <p>\n" +
                "  本案恢复审理。本院恢复审理后，另行组成合议庭，公开开庭进行了审理。湖南省宁乡市人民检察院指派检察员鲁海滨、检察官助理张青松出庭支持公诉。被告人周志勇及其辩护人谢扬驰到庭参加了诉讼。本案现已审理终结。</p>\n" +
                " <p>\n" +
                "  湖南省宁乡市人民检察院指控：2013年7月初的一天，被告人周志勇向被害人蔡某1谎称自己中标楚天科技股份有限公司信息大楼工程建设及设备安装工程，但存在资金缺口，邀请被害人蔡某1入伙经营，被害人蔡某1不同意入伙，但表示可以借款给被告人周志勇。为取得被害人信任，被告人周志勇向被害人蔡某1提供一份伪造的楚天科技股份有限公司的中标通知书及质保金催收附件，以此向被害人蔡某1借款200万元，约定月息8万元，三个月还本，并先支付给了被害人一个月的利息。被害人蔡某1遂按照被告人周志勇的要求，于2013年7月10日向楚天科技股份有限公司的建设银行账户转账200万元。2013年7月11日，上述200万元款项到达楚天科技股份有限公司账户后，被告人周志勇立即以自己汇错款为由向该公司申请将上述200万元返还。但该公司规定，上述款项只能返回原汇款人账户，被告人周志勇便以自己的头像和被害人蔡某1的身份信息，在宁乡市一办假证摊点伪造了一张被害人蔡某1的假身份证，并于2013年7月11日以被害人蔡某1的名义在建设银行宁乡市城北支行申领了账号为6217&times;&times;&times;&times;5892的建设银行卡。当日，被告人周志勇持被害人蔡某1的上述假身份证及银行卡向楚天科技股份有限公司申请将200万元退回。2013年7月12日，楚天科技公司将200万元汇入被告人周志勇申领的账号为6217&times;&times;&times;&times;5892的银行卡账户内后，被告人周志勇立即于7月12日至7月17日陆续将卡内的200万元予以消费、转账或取现。而后，被告人周志勇陆续支付了被害人蔡</p>\n" +
                " <p>\n" +
                "  3</p>\n" +
                " <p>\n" +
                "  德超40万元左右的利息和15万元的本金，剩余款项至今未退还给被害人蔡某1。</p>\n" +
                " <p>\n" +
                "  2017年3月11日，被告人周志勇被xx机关刑事拘留。</p>\n" +
                " <p>\n" +
                "  为证明上述事实，公诉机关列举了以下证据：被害人蔡某1的陈述，证人张某1、周某1、曾某、欧某、刘某1等人的证言，被告人伪造的中标文书及附件的复印件，被告人伪造的被害人蔡某1的身份证复印件一份及退款申请，中国建设银行电子汇划收款回单复印件，湖南农村商业银行结算业务申请书复印件，银行开某申请表，银行卡领卡签收单复印件，银行流水，楚天科技股份有限公司提供的合同复印件等，情况说明，户籍资料，到案经过，周志勇被网上追逃的情况说明，周志勇被刑事拘留的事由及时间的情况说明，网上追逃材料，被告人周志勇的供述和辩解。</p>\n" +
                " <p>\n" +
                "  该院认为，被告人周志勇虚构事实、隐瞒真相，骗取被害人财物192万元，数额特别巨大，应当以诈骗罪追究其刑事责任。其具有以下量刑情节：被告人周志勇到案后如实供述了案件事实，系坦白，可以从轻处罚。被告人周志勇系初犯、偶犯，可以酌情从轻处罚。被告人周志勇案发后已退还被害人约55万元，可以在量刑时酌情考虑。综上，建议对被告人周志勇犯诈骗罪判处法定刑为十年以上十一年以下有期徒刑，并处罚金。根据《中华人民共和国刑事诉讼法》第一百七十六条之规定，提起公诉，请求依法判处。</p>\n" +
                " <p>\n" +
                "  庭审中，被告人周志勇对起诉书指控的事实没有异议，辩称其有罪，但认为其不具有非法占有为目的，不构成诈骗罪。</p>\n" +
                " <p>\n" +
                "  被告人周志勇的辩护人辩称被告人周志勇对该笔借款不具有非法占有目的，其不构成诈骗罪，根据在案证据其可能构成伪造身份证件罪或者妨害信用卡管理罪。具体理由：一、在案</p>\n" +
                " <p>\n" +
                "  4</p>\n" +
                " <p>\n" +
                "  证据不足以证明被告人周志勇具有非法占有的目的，指控被告人周志勇构成诈骗罪事实不清，证据不足。1、被告人周志勇找蔡某1借款时签订了《借款协议》，其和妻子曾某均向蔡某1提供了真实有效的身份信息；2、被告人周志勇向蔡某1借款时有一定的经济实力，具备偿还该笔借款的能力，蔡某1也明知这一点；3、被告人周志勇虽采取了&ldquo;骗借&rdquo;的手段，但这系民事欺诈，不能认定为刑事欺诈；4、被告人周志勇有实际的履约行为和积极的履约态度；5、无证据证明周志勇对200万元借款有肆意挥霍行为；6、被告人周志勇一时无法偿还借款系生意失败的客观原因，并非根本不打算偿还借款；7、蔡某1非法收取年利率48%的高息，拒绝周志勇想提前偿还本金的合理请求，案发后又拒绝被告人周志勇提出停息并分期偿还本金的请求，也是导致被告人周志勇未及时还款的一个原因；8、被告人周志勇在出现资金困难无法按期还款后，并未逃逸，也没有隐匿财产，其本人和亲友始终在与蔡某1协商；二、被告人周志勇只可能涉嫌犯伪造身份证件罪或者妨害信用卡管理罪；三、周志勇家属已和被害人蔡某1达成了分期还款的《和解协议》，清偿了88万元借款，取得了被害人蔡某1的充分谅解，无论周志勇涉嫌何罪，因本案引发的社会矛盾都已得到了有效化解。</p>\n" +
                " <p>\n" +
                "  被告人周志勇未向法庭提交证据，其辩护人谢扬驰向本院提交了四组证据：</p>\n" +
                " <p>\n" +
                "  第一组证据：周某2股本收据、张某2出具的收条两张、周志勇的行驶证、姜某1出具的收条，拟证明：1、被告人周志勇向蔡某1借款时一直从事多种正当生意；2、被告人周志勇向蔡某1借款时有较为稳定的收益来源；3、被告人周志勇借款时有房有车，具备归还借款的经济实力。</p>\n" +
                " <p>\n" +
                "  第二组证据：证人陈某1、陈某2、姜某1、张某2、周</p>\n" +
                " <p>\n" +
                "  5</p>\n" +
                " <p>\n" +
                "  建兵、周某2、付某、琚某、肖某、朱某的调查笔录，拟证明：1、被告人周志勇借款时具备偿还借款的能力；2、蔡某1发现被骗前，被告人周志勇提出先偿还蔡某1一百万，但蔡某1拒绝；3、被告人周志勇出现履约困难系生意投资失败；4、被告人周志勇自始至终与蔡某1协商还款事宜，从未实施逃逸、隐匿财产、肆意挥霍等行为。</p>\n" +
                " <p>\n" +
                "  第三组证据：证人蔡某2、陈某1、吴某、周某2、曾某的调查笔录，被害人蔡某1的调查笔录以及被告人周志勇的会见笔录，拟证明：1、被告人周志勇以真实身份签订了借款协议；2、被告人周志勇借款时具备偿还借款的能力；3、蔡某1发现被骗前被告人周志勇提出先还蔡某1一百万，蔡某1拒绝；4、被告人周志勇出现履约困难系生意投资失败；5、报案后蔡某1拒绝周志勇停息还本请求；6、被告人周志勇自始至终与蔡某1协商还款事宜，从未实施逃逸、隐匿财产、肆意挥霍等行为。</p>\n" +
                " <p>\n" +
                "  第四组证据：和解协议、电子回单、请求报告、星唐公路分包合同，拟证明：1、蔡某1与曾某、周志勇达成和解协议；2、被告人周志勇平时表现良好，涌泉山村村委会及党支部、老粮仓镇星石村村民委员会、锦熙石材有限公司请求对被告人周志勇从轻判决。</p>\n" +
                " <p>\n" +
                "  经审理查明：2013年7月初的一天，被告人周志勇向蔡某1谎称自己中标楚天科技股份有限公司信息大楼工程建设及设备安装工程，但存在资金缺口，邀请蔡某1入伙经营，蔡某1不同意入伙，但表示可以借款给被告人周志勇。为取得蔡某1的信任，被告人周志勇向蔡某1提供一份其伪造的楚天科技股份有限公司的中标通知书及质保金催收附件，以此向蔡某1借款本金200万元，约定月息4分，即每月利息8万元，借款期</p>\n" +
                " <p>\n" +
                "  6</p>\n" +
                " <p>\n" +
                "  限为3个月，签订借款协议当天，被告人周志勇向蔡某1预先支付了一个月利息8万元。2013年7月10日，蔡某1遂按照被告人周志勇的要求，用其名下湖南宁乡农村商业银行股份有限公司（银行卡尾号为8354）于分两次共计向楚天科技股份有限公司的建设银行账户（尾号为4071）转账200万元。2013年7月11日，上述200万元款项到达楚天科技股份有限公司账户后，被告人周志勇立即以自己汇错款为由向该公司申请将上述200万元返还。但该公司规定，上述款项只能返回原汇款人账户，被告人周志勇便以自己的头像和蔡某1的身份信息，在宁乡市一办假证摊点伪造了一张蔡某1的假身份证，并于2013年7月11日以蔡某1的名义在建设银行宁乡市城北支行申领了账号为6217&times;&times;&times;&times;5892的建设银行卡。当日，被告人周志勇持蔡某1的上述假身份证及银行卡向楚天科技股份有限公司申请将200万元退回。2013年7月12日，楚天科技公司将200万元汇入被告人周志勇在建设银行申领的账号为6217&times;&times;&times;&times;5892户名为蔡某1的银行卡账户内，被告人周志勇立即于7月12日至7月17日陆续将该卡内的200万元予以套现消费、转账或取现。2013年7月11日至2013年11月底期间，被告人周志勇陆续向蔡某1支付了共计5个月利息，共计40万元，时至2013年12月16日，因被告人周志勇未及时支付利息，蔡某1向宁乡市xx机关报案。2013年12月18日，宁乡市xx局对该案立案侦查。</p>\n" +
                " <p>\n" +
                "  2017年3月11日，被告人周志勇被xx机关抓获并刑事拘留。</p>\n" +
                " <p>\n" +
                "  另查明，被告人周志勇在将该笔200万元款项从楚天科技股份有限公司转移出来不久，其有向蔡某1提出先行偿还部分本金的事实，但双方协商未果。蔡某1向xx机关报案后，被</p>\n" +
                " <p>\n" +
                "  7</p>\n" +
                " <p>\n" +
                "  告人周志勇被抓获前，被告人周志勇通过其家属、朋友多次与蔡某1协商还款事宜。</p>\n" +
                " <p>\n" +
                "  再查明，被告人周志勇在2013年7月至2013年12月期间，其经营的项目有长沙致发电器贸易有限公司、郴州临武道路检测项目、投资煤炭生意，以及被告人周志勇名下有湘Ａ0&times;&times;&times;&times;宝马轿车一辆（市值约40万元），宁乡市滨江新外滩2栋1单元1802室房产一套。在本案审理过程中，2021年10月11日，被告人周志勇以及亲属与蔡某1达成和解协议，协议约定蔡某1同意被告人周志勇只偿还120万元，协议当天支付30万元，剩余款项约定了明确的偿还方式及期限，并同意对被告人周志勇予以谅解。</p>\n" +
                " <p>\n" +
                "  上述事实，有如下经庭审质证、认证的证据予以证实，本院予以确认：</p>\n" +
                " <p>\n" +
                "  1、证人蔡某1的证言，证实2013年7月8日，被告人周志勇找到其，给其看了一份楚天科技的中标材料的复印件和附件，材料的主要内容是2013年7月4日他以湖南禹班建设集团有限公司项目负责人的身份与楚天科技股份有限公司签订了一份协议，协议上写明湖南禹班建设集团有限公司于2013年6月29日向楚天科技提交了招标文件，现已中标，规定由湖南禹班建设集团有限公司负责建设楚天科技公司信息大楼工程建设及设备安装。附件注明在收到中标通知书3日内（也就是2013年7月7日之前）需交质保金200万元，钱汇入楚天科技在建行的账号4300&times;&times;&times;&times;4071，在工程量进行到30％时经工程量确认及质保金银行付款凭证及经手人本人及身份证领取保证金的50%，工程量进行到50％经工程量确定及经手人本人及身份证领取保证金的50%，落款法定代表人签名都是周飞跃，审核人是周飞跃、张英，盖的公章是长沙楚天科技股份有限公司工程部。</p>\n" +
                " <p>\n" +
                "  8</p>\n" +
                " <p>\n" +
                "  当时周志勇还给其看了楚天科技绘制的工程设计图纸和预算表、他和他妻子曾某的身份证，并提供了家庭住址等。周志勇说他没有这么多钱交质保金，邀其合伙投资。其觉得机会难得，而且楚天科技在工程量进行到30%和50%时各返还100万元也必须由其本人带身份证及银行付款凭证才能领钱，所以其也没有去楚天科技了解情况，就于2013年7月10日在湖南农商银行转账200万元到了楚天科技建行4300&times;&times;&times;&times;4071的银行账户上。2013年9月至12月期间，其向周志勇了解情况但周志勇总是推诿，后来周志勇电话无法打通，人也消失找不到了，遂向xx机关报案。还证实，被告人周志勇提供的&ldquo;蔡某1&rdquo;身份证复印件是假的，这张身份证复印件上的姓名、出生日期、身份证号码、住址等信息都是蔡某1的身份信息，但上面照片是被告人周志勇的。蔡某1自己也没有在2013年7月11日向建行宁乡支行申请开某，卡号为6217&times;&times;&times;&times;5892的建行卡是周志勇使用其身份信息办理一张假身份证，然后再用这张假身份证在建行申请办理下来的。同时证明周志勇借款时先付了1个月8万元的利息，共付了5个月的利息40万元，被告人周志勇被刑事拘留后他家属又付了其18万元的事实。此外，蔡某1在检察机关的陈述，证实被告人周志勇在案发前确有向其提出偿还部分本金的事实。</p>\n" +
                " <p>\n" +
                "  2、证人张某1的证言，证实其是楚天科技股份有限公司工程部部长。2013年10月份因为公司招标建设信息大楼的中央空调，周志勇来投过标，后来中标价是50多万元，中标人不是周志勇，周志勇不是湖南禹班建设有限公司的人。蔡某1提供的2013年7月4日楚天科技公司关于信息大楼工程建设及设备安装与周志勇签订的协议复印件及附件，其认真看过发现是假的，此中标书及附件均为伪造的。其所在工程部没有公章，也没有</p>\n" +
                " <p>\n" +
                "  9</p>\n" +
                " <p>\n" +
                "  叫张英的人。</p>\n" +
                " <p>\n" +
                "  3、证人周某1的证言，证实其是楚天科技财务部工作人员。2013年7月10日，楚天科技公司账号收到一笔200万元的转账。第二天，一名陌生男子打电话说昨天有一笔200万元的转账到了其公司，是转错了款。2013年7月11日，财务总监李刚通知其和财务副部长文涛，说一个自称蔡某1的男子说他于7月10日误转了200万元到楚天科技的建设银行账号，现在要求退款，并说他腿脚不方便，要其和文涛到他住的水晶郦城小区核实一下。于是其和文涛就到了水晶郦城找该男子核实，那个男子当时拄着拐杖，说他就是蔡某1，是别人催的急，加上是他老婆去转账，所以转错了，并提供了一张蔡某1的身份证原件及复印件。其和文涛要他在身份证复印件上写上一个因错误汇款要求退款的说明就离开了。后来文涛应该和李刚汇报了，然后李刚及财务部长肖云红就要其填写用款审批单并由他们签字，再由其通过网银制作汇款单由文涛复核后就于7月12日将200万元退到蔡某1提供的账号了。并证实了李刚是公司股东，现在退休回永州老家了，文涛现在长春分公司上班。</p>\n" +
                " <p>\n" +
                "  4、证人刘某2的证言，证实在2013年7月，周志勇给其打电话，说他准备在楚天科技公司的空调设备建设上投个标，但需要200万的质保金，现在还差几十万元，问其能不能帮点忙。2013年7月27日，周打其电话问准备好钱没有，其当时准备了10万元，就在宁乡县城鸿富酒店对面一大泓附近，他给其翻了一下他在楚天科技的招投标文件，其没有接过手看，当时是完全相信他有这回事，他还提了一大袋的现金给其看了一下，说8月初就要交质保金了，现在差几十万，让其帮点忙，到时候如果中标就给其一个项目做，并且归还欠的钱。其当时就交给他10万元现金，他复印了他的身份证正反面，并且写了借款</p>\n" +
                " <p>\n" +
                "  10</p>\n" +
                " <p>\n" +
                "  金额、签了名。7月30日，周志勇再次打电话说质保金还差一点，让其再凑10万元，当天其就通过银行转了9万元到周志勇的信用社账户上，然后见面再给了他1万元现金。又和3天前一样复印了身份证，写了10万元借条，并说借的20万元，都会在4个月内归还，如果中了标就给其项目做，并还20万元，如果没有就还本金及月息2分的利息。</p>\n" +
                " <p>\n" +
                "  5、证人曾某的证言，证实周志勇是其前夫，两人于2014年协议离婚，周志勇在楚天科技承包工程，需要找蔡某1借钱，因为当时两人是夫妻关系，借钱需要其到场，所以两人一起到了蔡某1公司签借款协议。当时是向蔡某1借款200万元，当场支付了8万元现金作为利息，其当时作为共同债务人也在借款协议上签了字，借款协议当时在两人手上有一份，后来不知道周志勇丢到哪里不见了。</p>\n" +
                " <p>\n" +
                "  6、证人欧某的证言，证实其是楚天科技股份有限公司工程部工程师，认识周志勇、刘某1两人，但不熟悉。2013年左右周志勇在楚天科技信息大楼中央空调安装工程项目投过标，但没有中标，刘某1是楚天科技信息大楼装修工程的中标单位，其于2013年左右将周志勇介绍给刘某1认识，但没有介绍过他们两人合伙承包楚天科技公司的装修工程。</p>\n" +
                " <p>\n" +
                "  7、证人刘某1的证言，证实其是在广东爱得威建设集团股份有限公司湖南分公司上班，负责工程工作，该公司于2012年和2014年均在楚天科技有工程承包，刘某1担任项目经理。与楚天科技的欧某在工程业务上有对接，并于2013年左右，经欧某介绍认识了周志勇，当时其与周志勇见面是与周谈的兑换汇票的事情。其没有和周志勇谈过合作工程项目的事情，也没有合作过承包工程项目。</p>\n" +
                " <p>\n" +
                "  8、证人蔡某1提供的由被告人周志勇伪造的中标文书及附</p>\n" +
                " <p>\n" +
                "  11</p>\n" +
                " <p>\n" +
                "  件复印件，内容为楚天科技股份有限公司信息大楼工程施工经审定确定湖南禹班建设集团有限公司（项目负责人周志勇）为工程建设、设备安装的中标人。附件内容是中标人在收到中标通知书3日内交质保金200万元，钱汇入楚天科技在建行的账号4300&times;&times;&times;&times;4071，在工程量进行到30％时经工程量确认及质保金银行付款凭证及经手人本人及身份证领取保证金的50%壹佰万元，工程量进行到50％经工程量确定及经手人本人及身份证领取保证金的50%壹佰万元，落款法定代表人签名都是周飞跃，审核人是周飞跃、张英，盖的公章是长沙楚天科技股份有限公司工程部。时间2013年7月4日。</p>\n" +
                " <p>\n" +
                "  9、被告人周志勇伪造的向楚天科技股份有限公司申请退款200万元的蔡某1身份证复印件。</p>\n" +
                " <p>\n" +
                "  10、楚天科技股份有限公司提供的中国建设银行电子汇划收款回单复印件6张，证实2013年7月11日蔡某1名下湖南宁乡农商银行账户（6221&times;&times;&times;&times;8354）汇入楚天科技账户（4300&times;&times;&times;&times;4071）分两笔共计200万元；2013年7月12日楚天科技公司账户（4300&times;&times;&times;&times;4071）汇入被告人周志勇实际控制的户名为蔡某1的中国建设银行账户（6217&times;&times;&times;&times;5892）分四笔共计200万元的事实。</p>\n" +
                " <p>\n" +
                "  11、蔡某1湖南农村商业银行结算业务申请书复印件，证实其于2013年7月10日分两次将200万元质保金汇入楚天科技建设银行账户的事实。</p>\n" +
                " <p>\n" +
                "  12、由周志勇控制的户名为蔡某1的建设银行账户（6217&times;&times;&times;&times;5892）的流水记录，证实2013年7月12日楚天科技转入200万元，2013年7月13日&mdash;17日，该200万元全部用于消费或者转账支取和取现支取的事实。</p>\n" +
                " <p>\n" +
                "  13、周志勇名下中国建设银行账户6215&times;&times;&times;&times;0099、</p>\n" +
                " <p>\n" +
                "  12</p>\n" +
                " <p>\n" +
                "  证人姜某2下中国建设银行账户6217&times;&times;&times;&times;9064、刘恋名下中国建设银行账户6275&times;&times;&times;&times;5453、刘杰名下中国建设银行账户6215&times;&times;&times;&times;2850、证人刘某2名下中国建设银行账户6215&times;&times;&times;&times;0057的银行卡余额及开某信息，证明200万元的资金流水情况。</p>\n" +
                " <p>\n" +
                "  14、户名为蔡某1的建设银行账号6217&times;&times;&times;&times;5892的开某申请表、银行卡领卡签收单及申领人身份证复印件，其中申领人身份证复印件中，头像及身份信息均系被害人蔡某1。</p>\n" +
                " <p>\n" +
                "  15、由蔡某1出具的谅解书一份。</p>\n" +
                " <p>\n" +
                "  16、楚天科技股份有限公司提供的相关合同、中标文书复印件，证明周志勇没有在该公司承包工程项目的情况。</p>\n" +
                " <p>\n" +
                "  17、被告人周志勇农商银行账号8101&times;&times;&times;&times;8222基本信息及交易流水。</p>\n" +
                " <p>\n" +
                "  18、宁乡市xx局刑侦大队干警出具的说明材料一份、宁乡市xx局刑侦大队出具周志勇被网上追逃的情况说明一份、宁乡市xx局刑侦大队出具的周志勇被刑事拘留的事由及时间情况的说明一份，网上追逃记录一份。</p>\n" +
                " <p>\n" +
                "  19、被告人周志勇的户籍资料及现实表现证明。</p>\n" +
                " <p>\n" +
                "  20、侦查机关出具的被告人周志勇到案经过说明。</p>\n" +
                " <p>\n" +
                "  21、长沙致发电器贸易有限公司信用信息，周某2的股本收据，证人陈某2、周某2、付某、琚某、肖某、曾某的调查笔录，综合证明被告人周志勇在2010年底至2014年初由周某2代为持有长沙芙蓉区五发电器经营部、长沙致发电器贸易有限公司部分股份，并实际经营公司相关事务的事实。</p>\n" +
                " <p>\n" +
                "  22、张某2出具的收条两张、证人张某2的调查笔录，证实被告人周志勇在2013年9月份向张某2交付现金40万元共同投资煤炭生意，后煤炭厂老板吴国兵跑路了，钱也没有追回</p>\n" +
                " <p>\n" +
                "  13</p>\n" +
                " <p>\n" +
                "  的事实。</p>\n" +
                " <p>\n" +
                "  23、姜某1出具的收条、证人姜某1、周某3的调查笔录，证实在2013年4月至10月期间，被告人周志勇与姜某1共同投资郴州市临武县投资道路区间测速道路监控工程，被告人周志勇实际投入56万元，并全部收回且有部分盈利的事实。</p>\n" +
                " <p>\n" +
                "  24、湘Ａ0&times;&times;&times;&times;宝马牌轿车行驶证，证人陈某1、朱某、蔡某2、吴某、周某2、曾某的调查笔录以及证人蔡某2的出庭证言，综合证实：1、被告人周志勇在2013年7月至12月期间，名下资产（湘Ａ0&times;&times;&times;&times;宝马牌轿车一辆、宁乡市滨江新外滩2栋1单元1802室房产一套）及投资项目情况；2、被告人周志勇在将该笔200万元转移出来之后，有向蔡某1提出先行偿还部分本金的事实，以及被告人周志勇事后未携款逃匿、隐匿财产、肆意挥霍，且有亲属、朋友参与积极协商还款的事实。</p>\n" +
                " <p>\n" +
                "  25、和解协议、电子回单，证实在本案审理过程中，蔡某1与被告人周志勇达成和解协议，并取得了蔡某1的谅解。</p>\n" +
                " <p>\n" +
                "  26、被告人周志勇的供述和辩解，证实其于2013年5月份，参加过楚天科技公寓楼和宿舍楼的中央空调招标，都没有中标。其想找楚天科技再做点项目但手头没有钱，便想找蔡某1借钱。7月份，其找到蔡某1说其有个楚天的项目，其想和他一起做。并将事先准备好的假中标通知书及附件给蔡某1看，蔡某1看完后表示不想做项目，但可以借200万元给其，并约定以每月4分的利息向蔡某1借200万元，每月利息就是8万元。两天之后，其和前妻曾某一起到了蔡某1的投资公司，借了200万元，且在当天带了8万元现金作为当月利息给了蔡某1，然后由蔡某1汇入楚天科技公司账户200万元。借款到账之后过了两天，其打电话给楚天财务说钱打错了需要退回，但是楚天财务部的告知其说可以退，但要原路退回，而且要本人来楚天财务部核</p>\n" +
                " <p>\n" +
                "  14</p>\n" +
                " <p>\n" +
                "  实。其就说脚不方便，请他们到水晶郦城来，楚天财务部的人也同意了。因为蔡某1汇钱的是农商银行，而楚天科技公司财务部用的是建设银行，所以楚天财务部要求其提供建行的账户。于是其当天办了一张假身份证，假身份张的头像是其自己，身份证上的内容是蔡某1的个人信息，这张假身份证其用完就丢了。然后其用这张假身份证在宁乡一环路的建行开了一个账户。后其就打电话给楚天财务部，他们就说要派人过来核实。之后其就在水晶郦城租户家和楚天财务部的两个人见面了，其就将假身份证及用假身份证办的建设银行卡给楚天财务部的人核实，后楚天财务部人的就要其在这张假身份证的复印件上写了一个情况说明，内容其不记得了，大概内容就是其打了一笔钱到楚天财务的账上，现申请退回，该款跟楚天科技无关。并用假身份证办的建设银行账户也写在了上面，账号其不记得了。之后，楚天财务部就将200万打到其用假身份证办的建设银行卡上。钱到账之后，其取现、转账，到实体店套现，大概花了一个星期左右将200万全部套了出来。到实体店套现其现在只记得到宁乡市一环路金道电器套出了30多万。这钱其一部分还了账，一部分被人骗了，还了蔡某140万元的利息，就没有了。其每个月付蔡某18万元的利息，一直付了五个月，共40万元，都是当面支付的现金，后来因为没钱了就没有还过钱了。并证明其2017年到案退还了蔡某118万元。</p>\n" +
                " <p>\n" +
                "  上述证据内容客观真实、取证程序合法且与本案相关联，证据之间能够相互印证，形成完成的证据锁链，足以证明本院查明的事实，本院予以采信；关于被告人周志勇的辩护人提交的证据：周某2的股本收据，张某2出具的收条2张，周志勇的行驶证，姜某1出具的收条，证人陈某1、陈某2、姜某1、张某2、周某3、周某2、付某、琚某、肖某、朱某、</p>\n" +
                " <p>\n" +
                "  15</p>\n" +
                " <p>\n" +
                "  蔡某2、吴某、曾某的调查笔录，和解协议以及曾某的电子回单，公诉人对上述证据均无异议，取证程序符合法律规定，且能够印证本案的事实，本院予以采信；关于被告人周志勇的辩护人提交的被害人蔡某1的调查笔录，该份调查笔录虽经被害人蔡某1同意，但事先并未取得本院许可，取证程序不符合法律规定，故本院不予采信；关于被告人周志勇的辩护人提交的被告人周志勇的会见笔录，刑事诉讼法明确规定讯问犯罪嫌疑人应由人民检察院或者xx机关的侦查人员负责进行，故辩护人对被告人周志勇的会见笔录，不符合法律规定，亦不符合法定的证据形式，故本院不予采信；关于被告人周志勇的辩护人提交的请求报告、星塘公路分包合同，本院认为该两份证据与本案的事实没有关联性，本院不予采信。</p>\n" +
                " <p>\n" +
                "  本院认为，本案的争议焦点为：1、被告人周志勇在骗取借款时，是否具有非法占有的目的，是否构成诈骗罪？2、被告人周志勇是否构成伪造身份证件罪还是构成妨害信用卡管理罪？</p>\n" +
                " <p>\n" +
                "  关于争议焦点1，被告人周志勇在骗取借款时，是否具有非法占有的目的，是否构成诈骗罪？诈骗罪是指以非法占有为目的，采用虚构事实或隐瞒真相的方法骗取数额较大的公私财物的行为。由此可见，诈骗罪的构成要件要求行为人不仅在客观上具有采取虚构事实或者隐瞒真相的方法骗取数额较大的公私财物，还要求行为人在主观上表现为直接故意，并且具有非法占有公私财物的目的。但是&ldquo;以非法占有为目的&rdquo;的认定主观性相当强，难以通过客观事实直接证明，故对&ldquo;以非法占有为目的&rdquo;的认定既要避免单纯根据损害后果进行客观归罪，也不能仅凭行为人自己的供述定罪，而必须坚持在客观基础上进行主观判断，即在查明客观事实的前提下，根据一定的经验法则或者逻辑规则，推定行为人的主观目的。如何判断行为人是否</p>\n" +
                " <p>\n" +
                "  16</p>\n" +
                " <p>\n" +
                "  具有非法占有的目的，最典型的诈骗案件是往往针对陌生人。在这类案件中，被害人不知道犯罪分子家庭地址，犯罪分子一旦骗得被害人财物就逃之夭夭，切断与被害人的联系，非法占有的目的非常明显，对这类案件的非法占有目的的判断，在实践中不会产生争议。但是，在熟人之间，判断行为骗取财物是否属于诈骗，就要正确判定行为人是否具有非法占有的目的。本院认为，关于非法占有为目的的判断，结合金融类诈骗犯罪的相关司法解释，并充分考虑诈骗罪与金融诈骗类犯罪的共性，应当从以下几个方面进行综合判断：（1）事前，行为人有无归还能力；（2）事中，行为人有无积极归还或者消极不归还的行为或者表现；（3）事后，行为人有无逃避偿还款物的行为，即行为人取得财物后逃匿，躲避被害人催债；或者将财物转移、隐匿，拒不归还；或者将财物用于赌博、挥霍等或者其他违法犯罪活动，致使无法返还；（4）被骗人是否能够通过民事途径进行救济。一般来说，民事欺诈不具有非法占有为目的，且欺骗行为尚不严重，不影响被欺诈方通过民事途径进行救济，不宜轻易认定为诈骗犯罪，也符合刑法的谦抑性原则。</p>\n" +
                " <p>\n" +
                "  本案中，被告人周志勇伪造了楚天科技股份有限公司信息大楼工程施工中标通知书及质保金催收附件，通过虚构自己与楚天科技股份有限公司有承包项目工程，需要向该公司缴纳质保金的事实，使蔡某1产生错误认识将自己的200万元汇入楚天科技股份有限公司账户上。而后，被告人周志勇利用虚假身份信息，以蔡某1的名义办理了银行卡，将涉案的200万元汇入其控制的银行卡账户内，短时间内将上述款项套现、消费、转账或取现。对于被告人周志勇采取虚构事实和隐瞒真相的方式，将蔡某1的200万元转移至其控制之下的事实，控辩双方均无异议，但被告人周志勇对该笔款项是否具有非法占有的目</p>\n" +
                " <p>\n" +
                "  17</p>\n" +
                " <p>\n" +
                "  的则是本案的关键。结合本院采信的证据和认定的案件事实，双方签订了借款协议，约定借款本金200万元，月息4分，即利息8万元／月，借款期限3个月。被告人周志勇将该笔200万元款项转移出来之后，陆续向蔡某1支付了5个月利息，共计40万元的客观事实，有被告人周志勇供述与辩解、证人蔡某1的证言以及证人曾某的证言证明，且能相互印证。在本案发回重审阶段，公诉机关以及被告人周志勇的辩护人均向本院提交了新的证据，在案证据能够印证被告人周志勇在骗取借款时具备一定的偿还能力，且蔡某1在提供款项时对被告人周志勇的偿还能力有一定的了解，被告人周志勇在将该笔款项转移出来不久，有向蔡某1提出先偿还部分本金的事实，并按约连续5个月共计支付了蔡某140万元利息，同时没有证据充分证明被告人周志勇将该笔款项用于赌博、挥霍等违法事实，故本案在案证据，无法充分证明被告人周志勇在行为时对该笔款项具有非法占有的目的。此外，被告人周志勇与蔡某1以及证人曾某签订了借款协议，约定了借款利息及借款期限，虽在借款时具有一定欺诈行为，但并不影响蔡某1通过民事途径主张其权利，且同时双方在审理阶段达成了和解协议，蔡某1可另行通过民事途径进行救济。综上所述，现有证据虽能够印证被告人周志勇具有虚构事实和隐瞒真相骗取他人财物的行为，但现有证据无法充分证明被告人周志勇在行为时具有非法占有的目的，基于存疑有利于被告人的原则，以及刑法的谦抑性原则，故公诉机关对被告人周志勇犯诈骗罪的指控不成立。</p>\n" +
                " <p>\n" +
                "  关于争议焦点2，被告人周志勇是否构成伪造身份证件罪还是构成妨害信用卡管理罪？被告人周志勇在转移该笔款项过程中，伪造他人身份证件，并使用虚假的身份证明骗领信用卡，同时触犯伪造身份证件罪和妨害信用卡管理罪。结合本案事实，</p>\n" +
                " <p>\n" +
                "  18</p>\n" +
                " <p>\n" +
                "  被告人周志勇伪造他人身份证件，目的是为了使用该虚假身份证明骗领信用卡，并意图将该笔款项转移，系手段行为，同时被告人周志勇在本案中明显违背他人意愿，使用伪造的身份证明申领信用卡，其犯罪行为不仅侵犯了国家机关对社会的管理秩序，扰乱了公共秩序，同时也侵犯了社会主义市场经济秩序，以及金融管理秩序，以妨害信用卡管理罪对其行为进行评价更为适宜，故应当以妨害信用卡管理罪对被告人周志勇定罪处罚。</p>\n" +
                " <p>\n" +
                "  综上所述，本院认为，被告人周志勇使用虚假的身份证明骗领信用卡，妨害信用卡管理，其行为已构成妨害信用卡管理罪。被告人周志勇到案后，如实供述了伪造身份证并骗领中国建设银行卡的犯罪事实，可认定为坦白，依法可以从轻处罚。关于被告人周志勇在本案中的量刑，结合被告人周志勇骗领信用卡的行为及手段，以及该行为造成的社会影响等具体情节，本院在量刑时均予以综合考量。关于被告人周志勇及其辩护人提出不构成诈骗罪的辩护意见，本院在论述本案焦点争议时，已予综合评判；关于被告人周志勇的辩护人提出被告人周志勇只构成伪造身份证件罪或者妨害信用卡管理罪的辩护意见，经审理认为，对被告人周志勇以妨害信用卡管理罪予以评判更为适宜；关于被告人周志勇的辩护人提出被告人周志勇在本案中具有的量刑情节，本院在量刑时已予综合考虑。据此，依照《中华人民共和国刑法》第一百七十七条之一第一款第（三）项、第六十七条第三款、第五十二条、第五十三条以及《中华人民共和国刑事诉讼法》第二百三十九条之规定，判决如下：</p>\n" +
                " <p>\n" +
                "  被告人周志勇犯妨害信用卡管理罪，判处有期徒刑一年九个月，并处罚金人民币三万元（已预缴）。</p>\n" +
                " <p>\n" +
                "  （刑期从判决执行之日起计算，判决执行以前先行羁押的，羁押一日折抵刑期一日，折抵原已羁押14日，即自2020年5</p>\n" +
                " <p>\n" +
                "  19</p>\n" +
                " <p>\n" +
                "  月7日起至2022年1月23日止）</p>\n" +
                " <p>\n" +
                "  如不服本判决，可在接到判决书的第二日起十日内，通过本院或者直接向湖南省长沙市中级人民法院提出上诉。书面上诉的，应提交上诉状正本一份，副本二份。</p>\n" +
                " <p align=\"right\">\n" +
                "  审　判　长　张　示</p>\n" +
                " <p align=\"right\">\n" +
                "  人民陪审员　谢　娇</p>\n" +
                " <p align=\"right\">\n" +
                "  人民陪审员　冯　茜</p>\n" +
                " <p align=\"right\">\n" +
                "  二〇二二年一月十八日</p>\n" +
                " <p align=\"right\">\n" +
                "  法官　助理　杨朝磊</p>\n" +
                " <p align=\"right\">\n" +
                "  书　记　员　贺珍珍</p>\n" +
                " <p>\n" +
                "  20</p>\n" +
                " <p>\n" +
                "  附相关法律条文：</p>\n" +
                " <p>\n" +
                "  《中华人民共和国刑法》第一百七十七条之一有下列情形之一，妨害信用卡管理的，处三年以下有期徒刑或者拘役，并处或者单处一万元以上十万元以下罚金；数量巨大或者有其他严重情节的，处三年以上十年以下有期徒刑，并处二万元以上二十万元以下罚金：</p>\n" +
                " <p>\n" +
                "  （一）明知是伪造的信用卡而持有、运输的，或者明知是伪造的空白信用卡而持有、运输，数量较大的；</p>\n" +
                " <p>\n" +
                "  （二）非法持有他人信用卡，数量较大的；</p>\n" +
                " <p>\n" +
                "  （三）使用虚假的身份证明骗领信用卡的；</p>\n" +
                " <p>\n" +
                "  （四）出售、购买、为他人提供伪造的信用卡或者以虚假的身份证明骗领的信用卡的。</p>\n" +
                " <p>\n" +
                "  窃取、收买或者非法提供他人信用卡信息资料的，依照前款规定处罚。</p>\n" +
                " <p>\n" +
                "  银行或者其他金融机构的工作人员利用职务上的便利，犯第二款罪的，从重处罚。</p>\n" +
                " <p>\n" +
                "  第六十七条犯罪以后自动投案，如实供述自己的罪行的，是自首。对于自首的犯罪分子，可以从轻或者减轻处罚。其中，犯罪较轻的，可以免除处罚。</p>\n" +
                " <p>\n" +
                "  被采取强制措施的犯罪嫌疑人、被告人和正在服刑的罪犯，如实供述司法机关还未掌握的本人其他罪行的，以自首论。</p>\n" +
                " <p>\n" +
                "  犯罪嫌疑人虽不具有前两款规定的自首情节，但是如实供述自己罪行的，可以从轻处罚；因其如实供述自己罪行，避免特别严重后果发生的，可以减轻处罚。</p>\n" +
                " <p>\n" +
                "  第五十二条判处罚金，应当根据犯罪情节决定罚金数额。</p>\n" +
                " <p>\n" +
                "  第五十三条罚金在判决指定的期限内一次或者分期缴纳。期满不缴纳的，强制缴纳。对于不能全部缴纳罚金的，人民法</p>\n" +
                " <p>\n" +
                "  21</p>\n" +
                " <p>\n" +
                "  院在任何时候发现被执行人有可以执行的财产，应当随时追缴。</p>\n" +
                " <p>\n" +
                "  由于遭遇不能抗拒的灾祸等原因缴纳确实有困难的，经人民法院裁定，可以延期缴纳、酌情减少或者免除。</p>\n" +
                " <p>\n" +
                "  《中华人民共和国刑事诉讼法》第二百三十九条原审人民法院对于发回重新审判的案件，应当另行组成合议庭，依照第一审程序进行审判。对于重新审判后的判决，依照本法第二百二十七条、第二百二十八条、第二百二十九条的规定可以上诉、抗诉。</p>\n" +
                " <p>\n" +
                "  &nbsp;</p>\n" +
                "</div>\n" +
                "<p>\n" +
                " &nbsp;</p>\n";
        Document parse = Jsoup.parse(html);
        Elements elements = parse.select(".PDF_pox");
        System.out.println(elements.size());
        System.out.println(parse.select("div").size());
        Elements allElements = parse.getAllElements();
        System.out.println(allElements.size());
        Elements body = parse.getElementsByTag("body").get(0).children();
        List<DataNode> nodes = parse.getElementsByTag("body").get(0).dataNodes();
        List<String> body1 = parse.getElementsByTag("body").eachText();
        parse.getElementsByTag("body");
        System.out.println(body.size());
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
                mdp.addAltChunk(AltChunkType.Html, html.getBytes(StandardCharsets.UTF_8));

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
        String str = "原告襄阳金照普惠网络科技有限公司向本院提出诉讼请求";
        String[] split = str.split("。");
        System.out.println(split[0]);


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
        String amount = "七千三六十四";
        Matcher matcher = AMOUNT_PATTERN.matcher(amount);
        System.out.println(matcher.find());
        int i = NumberChineseFormatter.chineseToNumber(amount);
        System.out.println(i);
    }

    @Test
    public void test7() {
        StringBuilder county = new StringBuilder();
        for (Term term : ToAnalysis.parse("被告刘明明、王丽在原告处借款50万元")) {
            System.out.println(term.getNatureStr() + "=====" + term.getRealName());
        }
        System.out.println(county.toString());
        // System.out.println(new StringBuilder("住所地重庆市永川区双石镇杨家湾").reverse().toString());
/*        String str = "广州知识产权法院";
        int index = str.indexOf("知识产权法院");
        System.out.println(str.substring(0,index));*/
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
    public void test15() {
        String temp = "申请机关宁波市强制医疗所";
        String[] split = temp.split("，");
        int start = temp.lastIndexOf("年");
        int end = temp.lastIndexOf("日");
        if (end == -1) {
            end = temp.lastIndexOf("月");
        }
        if (end > start) {
            System.out.println(temp.substring(start - 4, end + 1));
        }

    }

    @Test
    public void test16() {
        String sentence = "经海南省安宁医院精神疾病司法鉴定中心鉴定:1、林慧诗在本次作案时患有妄想阵发和人格障碍;2、林慧诗对本次作案无刑事责任能力";
        int start = sentence.indexOf("患");
        if (start == -1) {
            start = sentence.indexOf("系");
        }
        int end = sentence.lastIndexOf("症");
        if (end == -1) {
            end = sentence.lastIndexOf("碍");
        }
        if (end == -1) {
            end = sentence.lastIndexOf("病");
        }

        System.out.println(sentence.substring(start,end));

    }
}
