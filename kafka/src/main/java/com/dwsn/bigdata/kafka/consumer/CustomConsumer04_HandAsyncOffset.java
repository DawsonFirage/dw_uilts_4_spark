package com.dwsn.bigdata.kafka.consumer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class CustomConsumer04_HandAsyncOffset {
    public static void main(String[] args) {
        /*
          虽然同步提交offset更可靠一些，但是由于其会阻塞当前线程，直到提交成功。因此吞吐量会受到很大的影响。
          因此更多的情况下，会选用异步提交offset的方式。
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

            // 异步提交offset -- 与同步提交的唯一区别
            kafkaConsumer.commitAsync();
        }

    }
}
