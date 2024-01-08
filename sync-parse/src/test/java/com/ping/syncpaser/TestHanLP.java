package com.ping.syncpaser;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.model.crf.CRFLexicalAnalyzer;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

public class TestHanLP {

    @Test
    public void test1() throws IOException {
        Segment segment = HanLP.newSegment().enablePlaceRecognize(true);
        System.out.println(segment.seg("刘春利找到孙某1后持刀向孙某1腹部捅刺，孙某1躲闪一下被扎住左侧脸部"));
        List<Term> termList = StandardTokenizer.segment("刘春利找到孙某1后持刀向孙某1腹部捅刺，孙某1躲闪一下被扎住左侧脸部");
        System.out.println(termList);
        System.out.println("--------NPL");
        System.out.println(NLPTokenizer.segment("刘春利找到孙某1后持刀向孙某1腹部捅刺，孙某1躲闪一下被扎住左侧脸部"));

        CRFLexicalAnalyzer analyzer = new CRFLexicalAnalyzer();
        System.out.println("---------");
        System.out.println(analyzer.analyze("刘春利找到孙某1后持刀向孙某1腹部捅刺，孙某1躲闪一下被扎住左侧脸部"));
        System.out.println(analyzer.seg("刘春利找到孙某1后持刀向孙某1腹部捅刺，孙某1躲闪一下被扎住左侧脸部"));
        System.out.println("--------");
        System.out.println(ToAnalysis.parse("刘春利找到孙某1后持刀向孙某1腹部捅刺，孙某1躲闪一下被扎住左侧脸部"));


    }
}
