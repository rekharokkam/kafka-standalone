package com.learning.spring.kafka.avro.kafkaconsumer;

import com.learning.spring.kafka.avro.Customer;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Slf4j
public class KafkaAvroConsumerV2 {

    private static final String TOPIC_NAME = "customer-avro-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String GROUP_ID = "kafka-avro-java-consumer2-group";
    private static final String OFFRESET_RESET_CONFIG = "earliest";

    private Properties kafkaAvroConsumerProperties = new Properties();

    public KafkaAvroConsumerV2 () {
        kafkaAvroConsumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaAvroConsumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        kafkaAvroConsumerProperties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        kafkaAvroConsumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFRESET_RESET_CONFIG);
        kafkaAvroConsumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer .class.getName());
        kafkaAvroConsumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        kafkaAvroConsumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        kafkaAvroConsumerProperties.setProperty("schema.registry.url", "http://localhost:8081");
        kafkaAvroConsumerProperties.setProperty("specific.avro.reader", "true");
    }

    public void consumeMessage () {
        KafkaConsumer <String, Customer> kafkaConsumer = new KafkaConsumer<String, Customer>(kafkaAvroConsumerProperties);
        kafkaConsumer.subscribe(Collections.singleton(TOPIC_NAME));

        System.out.println("Waiting for data");

        while (true) {
            ConsumerRecords<String, Customer> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(500));

            for (ConsumerRecord<String, Customer> consumerRecord: consumerRecords) {
                Customer customerV2 = consumerRecord.value();
                System.out.printf("Key : %s" , consumerRecord.key() + "\n\n");
                System.out.printf("Each customer V2 : %s", customerV2 + "\n\n");

                if (customerV2.getAge() > 60) {
                    System.out.println("Oh we have a veteran");
                }
            }

            kafkaConsumer.commitSync(); // to commit the offset
        }
    }

    public static void main(String[] args) {
        new KafkaAvroConsumerV2().consumeMessage();
    }
}
