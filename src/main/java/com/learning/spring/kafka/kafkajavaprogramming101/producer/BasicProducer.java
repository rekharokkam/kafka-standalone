package com.learning.spring.kafka.kafkajavaprogramming101.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class BasicProducer {

    private static final String TOPIC_NAME = "first-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";

    private Properties kafkaProducerProperties = new Properties();

    public BasicProducer () {
        kafkaProducerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaProducerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public void sendMessage () {
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProducerProperties);
        ProducerRecord <String, String> producerRecord =
                new ProducerRecord<>(TOPIC_NAME, "first message from Java producer");

        //asynchronous
        kafkaProducer.send(producerRecord);
        //flush data
        kafkaProducer.flush();
        //flush and close the topic
        kafkaProducer.close();    }

    public static void main(String[] args) {
        new BasicProducer().sendMessage();
    }
}
