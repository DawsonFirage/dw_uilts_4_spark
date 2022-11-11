package com.dawson.ansj.test;

import org.ansj.recognition.impl.StopRecognition;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

public class AnsjTest {

    public static void main(String[] args) throws IOException {

//        String str = "欢迎使用ansj_seg,(ansj中文分词)在这里如果你遇到什么问题都可以联系我.我一定尽我所能.帮助大家.ansj_seg更快,更准,更自由!张道鑫买菜,《开端》《镜双城》《淘金》三部热播剧均有她，你发现了吗？";
        String str = "赵恩龙买了一个超级大南瓜，结果却感染了新型冠状肺炎";
        System.out.println("无停用词");
        System.out.println(ToAnalysis.parse(str));

        /*
            设置过滤器
         */
        StopRecognition filter = new StopRecognition();
        // 导入停用词库
        List<String> stop_words = FileUtils.readLines(new File("analyzer-test/library/stop_library/哈工大停用词表.txt"), UTF_8);
        filter.insertStopWords(stop_words);

        filter.insertStopNatures();

        System.out.println("有停用词");
        System.out.println(ToAnalysis.parse(str).recognition(filter));

    }

}
