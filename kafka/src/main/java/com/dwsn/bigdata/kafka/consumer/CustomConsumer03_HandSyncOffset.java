package com.dwsn.bigdata.kafka.consumer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class CustomConsumer03_HandSyncOffset {
    public static void main(String[] args) {
        /*
          同步提交offset有失败重试机制，故更加可靠，但是由于一直等待提交结果，提交的效率比较低。
         */

        Properties properties = Common.getConsumerProperties("test");
        // 是否自动提交offset
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        kafkaConsumer.subscribe(Arrays.asList("first"));

        // 4 消费数据
        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(1));

            for (ConsumerRecord<String, String> consumerRecord :consumerRecords) {
                System.out.println(consumerRecord);
            }

            // 同步提交offset
            kafkaConsumer.commitSync();
        }

    }
}
