package com.learning.spring.kafka.kafkajavaprogramming101.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class BasicConsumerSeekAndAssign {
    private static final String TOPIC_NAME = "first-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String GROUP_ID = "first-java-consumer-group";
    private static final String OFFRESET_RESET_CONFIG = "earliest";

    private Logger logger = LoggerFactory.getLogger(BasicConsumerSeekAndAssign.class);

    private Properties kafkaConsumerProperties = new Properties();

    public BasicConsumerSeekAndAssign () {
        kafkaConsumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaConsumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        kafkaConsumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFRESET_RESET_CONFIG);
    }

    public void consumeMessage () {
        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(kafkaConsumerProperties);

        //Assign to a specific TopicPartition
        TopicPartition topicPartitionToReadFrom = new TopicPartition(TOPIC_NAME, 2);
        long offsetToreadFrom = 83L;

        //assign
        kafkaConsumer.assign(Arrays.asList(topicPartitionToReadFrom));

        //seek
        kafkaConsumer.seek(topicPartitionToReadFrom, offsetToreadFrom);

        boolean isKeepReading = true;
        int numberOfMessagesToBeRead = 2;
        int numberOfMessagesReadSoFar = 0;

        while (isKeepReading) {
            ConsumerRecords<String, String> messages = kafkaConsumer.poll(Duration.ofMillis(100));
            logger.info("total number of messages read : " + messages.count());
            for (ConsumerRecord<String, String> message : messages) {
                logger.info("Topic : " + message.topic());
                logger.info("Key : " + message.key() + ", Value : " + message.value());
                logger.info("Partition : " + message.partition() + " , Offset : " + message.offset() + "\n");

                if (++ numberOfMessagesReadSoFar >=  numberOfMessagesToBeRead){
                    isKeepReading = false;
                    break; //break the for loop
                }
            }
        }

        logger.info("Existing the Application");
        kafkaConsumer.close();

    }

    public static void main(String[] args) {

        new BasicConsumerSeekAndAssign().consumeMessage();
    }

}

