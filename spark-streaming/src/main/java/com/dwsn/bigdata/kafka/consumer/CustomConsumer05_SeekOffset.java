package com.dwsn.bigdata.kafka.consumer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class CustomConsumer05_SeekOffset {
    public static void main(String[] args) {
        /*
          指定Offset消费
         */

        Properties properties = Common.getConsumerProperties("test");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        kafkaConsumer.subscribe(Arrays.asList("first"));

        // 需要先获取topic的分区的信息
        Set<TopicPartition> assignment = new HashSet<>();
        while (assignment.size() == 0) {
            // 获取消费者分区分配信息（有了分区分配信息才能开始消费）
            kafkaConsumer.poll(Duration.ofSeconds(1));
            assignment = kafkaConsumer.assignment();
        }

        // 遍历所有分区，并指定offset从1700的位置开始消费
        for (TopicPartition tp : assignment) {
            kafkaConsumer.seek(tp, 1700);
        }

        // 4 消费数据
        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));

            for (ConsumerRecord<String, String> consumerRecord :consumerRecords) {
                System.out.println(consumerRecord);
            }
        }

    }
}
