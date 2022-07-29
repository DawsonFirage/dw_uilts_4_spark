package com.dwsn.bigdata.kafka.consumer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Properties;

public class CustomConsumer02_Partition {
    public static void main(String[] args) {
        // 1 消费者配置文件
        Properties properties = Common.getConsumerProperties("test");

        // 2 创建消费者对象
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        // 3 注册要消费的主题及分区（可以多个）
        ArrayList<TopicPartition> topics = new ArrayList<>();
        topics.add(new TopicPartition("first", 0));
        kafkaConsumer.assign(topics);

        // 4 消费数据
        while (true) {
            // 设置1s中消费一批数据
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));

            // 打印消费到的数据
            for (ConsumerRecord<String, String> consumerRecord :consumerRecords) {
                System.out.println(consumerRecord);
            }
        }

    }
}
