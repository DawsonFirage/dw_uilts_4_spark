package com.dawson.jieba.test;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.huaban.analysis.jieba.WordDictionary;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class JiebaTest {

    public static void main(String[] args) throws IOException {
        JiebaSegmenter segmenter = new JiebaSegmenter();

//        String sentences = "北京京天威科技发展有限公司大庆车务段的装车数量";
        String sentences = "我今天去逛西湖旁的菜市场";

        // 获取conf目录下的自定义词库
        WordDictionary.getInstance().init(Paths.get("analyzer-test/library/dic_library"));

        System.out.println(segmenter.sentenceProcess(sentences));

        String content = "《开端》《镜双城》《淘金》三部热播剧均有她，你发现了吗？";
        List<String> stop_words = FileUtils.readLines(new File("analyzer-test/library/stop_library/哈工大停用词表.txt"));
//        JiebaSegmenter segmenter = new JiebaSegmenter();
        List<String> result = segmenter.sentenceProcess(content);
        System.out.println("没有过滤停用词======" + result);
        result = result.stream().map(o -> o.trim()).filter(o -> !stop_words.contains(o)).collect(Collectors.toList());
        System.out.println("过滤停用词=========" + result);
    }

}
