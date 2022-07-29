package com.dwsn.bigdata.kafka.consumer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.TopicPartition;

import java.time.Duration;
import java.util.*;

public class CustomConsumer06_SeekOffsetForTime {
    public static void main(String[] args) {
        /*
          指定时间消费
          本质也是根据时间获取到Offset
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

        /*
          获取每个分区在指定时间位置的offset
         */
        HashMap<TopicPartition, Long> timestampToSearch = new HashMap<>();
        // 封装集合存储，每个分区对应一天前的数据
        for (TopicPartition tp : assignment) {
            timestampToSearch.put(tp, System.currentTimeMillis() - 1 * 24 * 3600 * 1000);
        }
        Map<TopicPartition, OffsetAndTimestamp> offsets = kafkaConsumer.offsetsForTimes(timestampToSearch);

        // 遍历所有分区，并指定offset开始消费的位置
        for (TopicPartition topicPartition : assignment) {
            OffsetAndTimestamp offsetAndTimestamp = offsets.get(topicPartition);
            // 根据时间指定开始消费的位置
            if (offsetAndTimestamp != null) {
                kafkaConsumer.seek(topicPartition, offsetAndTimestamp.offset());
            }
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
