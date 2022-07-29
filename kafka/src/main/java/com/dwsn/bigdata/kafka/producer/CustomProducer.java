package com.dwsn.bigdata.kafka.producer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class CustomProducer {
    public static void main(String[] args) {
        // 1 创建 kafka 生产者的配置对象
        Properties properties = new Properties();

        // 2 给kafka配置对象添加配置信息
        // 指定 bootstrap.servers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "node101:9092");
        // key,value序列化（必须）：key.serializer，value.serializer
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Common.KAFKA_STRING_SERIALIZATION_CLASS);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Common.KAFKA_STRING_SERIALIZATION_CLASS);

        // 3 创建kafka生产者对象
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        // 4 调用send方法,发送消息
        for (int i = 0; i < 5; i++) {
            kafkaProducer.send(new ProducerRecord<>("first", "message" + i));
        }

        // 5 关闭资源
        kafkaProducer.close();
    }
}
