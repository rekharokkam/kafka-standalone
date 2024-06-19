package com.learning.spring.kafka.kafkajavaprogramming101.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class BasicConsumer {

    private static final String TOPIC_NAME = "first-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String GROUP_ID = "first-java-consumer-group";
    private static final String OFFRESET_RESET_CONFIG = "earliest";

    private Logger logger = LoggerFactory.getLogger(BasicConsumer.class);

    private Properties kafkaConsumerProperties = new Properties();

    public BasicConsumer () {
        kafkaConsumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaConsumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        kafkaConsumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFRESET_RESET_CONFIG);
        kafkaConsumerProperties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        kafkaConsumerProperties.setProperty(ConsumerConfig.ISOLATION_LEVEL_CONFIG, "read_committed");
    }

    public void consumeMessage () {
        KafkaConsumer <String, String> kafkaConsumer = new KafkaConsumer<String, String>(kafkaConsumerProperties);
        kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME)); // can subscribe to more than one topic

        while (true) {
            ConsumerRecords<String, String> messages = kafkaConsumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> message : messages) {
                logger.info("Topic : " + message.topic());
                logger.info("Key : " + message.key() + ", Value : " + message.value());
                logger.info("Partition : " + message.partition() + " , Offset : " + message.offset() + "\n");
            }
        }

    }

    public static void main(String[] args) {

        new BasicConsumer().consumeMessage();
    }

}
