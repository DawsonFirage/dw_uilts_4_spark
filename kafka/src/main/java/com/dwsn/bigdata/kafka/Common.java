package com.dwsn.bigdata.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class Common {
    public static String KAFKA_STRING_SERIALIZATION_CLASS = StringSerializer.class.getName();
    public static String KAFKA_STRING_DESERIALIZER_CLASS = StringDeserializer.class.getName();

    public static Properties getConsumerProperties(String GroupId){
        // 1 消费者配置文件
        Properties properties = new Properties();

        // 2 添加配置参数
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "test:9092");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, KAFKA_STRING_DESERIALIZER_CLASS);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KAFKA_STRING_DESERIALIZER_CLASS);
        // 消费者组 -- 必须指定
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, GroupId);

        /**
         * 消费者组的分区策略
         * 1 Range + CooperativeSticky (默认)
         * 2 RoundRobin (生产常用)
         * 3 Sticky
          */
        // RoundRobin
        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, "org.apache.kafka.clients.consumer.RoundRobinAssignor");

        // Sticky
//        ArrayList<String> strategies = new ArrayList<>();
//        strategies.add("org.apache.kafka.clients.consumer.StickyAssignor");
//        properties.put(ConsumerConfig.PARTITION_ASSIGNMENT_STRATEGY_CONFIG, strategies);

        return properties;
    }

}
