package com.dwsn.bigdata.kafka.producer;

import com.dwsn.bigdata.kafka.Common;
import org.apache.kafka.clients.producer.*;

import java.util.Properties;

public class CustomProducerCallback {
    public static void main(String[] args) throws InterruptedException {
        // 1 创建 kafka 生产者的配置对象
        Properties properties = new Properties();

        // 2 给kafka配置对象添加配置信息
        // 指定 bootstrap.servers
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "test:9092");
        // key,value序列化（必须）：key.serializer，value.serializer
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, Common.KAFKA_STRING_SERIALIZATION_CLASS);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, Common.KAFKA_STRING_SERIALIZATION_CLASS);

        // 3 创建kafka生产者对象
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(properties);

        // 4 调用send方法,发送消息
        for (int i = 0; i < 50; i++) {
            // 添加回调
            kafkaProducer.send(new ProducerRecord<>("first", "message" + i)
                    , (metadata, exception) -> {
                            if (exception == null) {
                                // 没有异常，输出信息到控制台
                                System.out.println("主题：" + metadata.topic() + "->" + "分区：" + metadata.partition());
                            } else {
                                // 出现异常打印
                                exception.printStackTrace();
                            }
                        }
                    );

            // 延迟一会会看到数据发往不同分区
            Thread.sleep(2);
        }
    }
}
