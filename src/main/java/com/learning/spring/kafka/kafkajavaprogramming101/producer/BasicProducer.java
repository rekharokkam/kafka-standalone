package com.learning.spring.kafka.kafkajavaprogramming101.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.UUID;

public class BasicProducer {

    private static final String TOPIC_NAME = "first-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";

    private Properties kafkaProducerProperties = new Properties();

    public BasicProducer () {
        kafkaProducerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaProducerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducerProperties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        kafkaProducerProperties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "lz4");
        kafkaProducerProperties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        kafkaProducerProperties.setProperty(ProducerConfig.CLIENT_ID_CONFIG, "kafka_standalone");
        kafkaProducerProperties.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG, UUID.randomUUID().toString());
    }

    public void sendMessage () {
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<>(kafkaProducerProperties);
        ProducerRecord <String, String> producerRecord =
                new ProducerRecord<>(TOPIC_NAME, "first message from Java producer");

        //Fire and forget model
        kafkaProducer.send(producerRecord);
        //flush data
        kafkaProducer.flush();
        //flush and close the topic
        kafkaProducer.close();    }

    public static void main(String[] args) {
        new BasicProducer().sendMessage();
    }
}
