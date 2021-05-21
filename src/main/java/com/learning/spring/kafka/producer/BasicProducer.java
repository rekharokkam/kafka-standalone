package com.learning.spring.kafka.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class BasicProducer {

    Properties producerProperties = new Properties();

    public BasicProducer () {
        producerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        producerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        producerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    public void sendMessage () {
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(producerProperties);
        ProducerRecord <String, String> producerRecord =
                new ProducerRecord<>("first-topic", "first message from Java producer");

        //asynchronous
        kafkaProducer.send(producerRecord);
        kafkaProducer.flush();
        kafkaProducer.close();
    }

    public static void main(String[] args) {
        BasicProducer producer = new BasicProducer();
        producer.sendMessage();
    }
}
