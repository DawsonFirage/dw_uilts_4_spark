package com.dwsn.bigdata.kafka.producer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class CustomProducerSync {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Kafka Producer配置
        Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "test:9092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Common.KAFKA_STRING_SERIALIZATION_CLASS);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Common.KAFKA_STRING_SERIALIZATION_CLASS);

        KafkaProducer<String, String> producer = new KafkaProducer<>(properties);

        for (int i = 0; i < 10; i++) {
            // 异步发送
//            producer.send(new ProducerRecord<>("first", "kafka" + i));

            // 同步发送 -- 异步发送后加get()
            producer.send(new ProducerRecord<>("first", "kafka" + i)).get();
        }

        producer.close();
    }
}
