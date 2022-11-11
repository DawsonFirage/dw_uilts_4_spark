package com.dawson.ansj.udf;

import org.ansj.domain.Result;
import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.DicAnalysis;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author Dawson
 */
public class Udf {

    public static void main(String[] args) throws IOException {
        String analysis = analysis("赵恩龙买了一个超级大南瓜，结果却感染了新型冠状肺炎。所以他只能在家看热播剧——诡异的大朵可");
        System.out.println(analysis);
    }

    /**
     * 对内容进行分词，返回以','切分的结果集
     * @param content
     * @return
     * @throws IOException
     */
    public static String analysis(String content) throws IOException {
        /*
            初始化过滤器
         */
        StopRecognition filter = new StopRecognition();
        // 导入停用词库
        List<String> stop_words = FileUtils.readLines(new File("analyzer-test/library/stop_library/哈工大停用词表.txt"), UTF_8);
        filter.insertStopWords(stop_words);

        Result recognition = DicAnalysis.parse(content).recognition(filter);
        return recognition.toStringWithOutNature();
    }

}
