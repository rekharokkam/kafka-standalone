package com.learning.spring.kafka.avro.avroinjava;

import com.learning.spring.kafka.avro.CustomerV1;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class KafkaAvroProducerV1 {

    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String CUSTOMER_AVRO_TOPIC_NAME = "customer-topic";
    private static final String MESSAGE_KEY = "customer_v1";

    public static void main(String[] args) {

       Properties kafkaAvroProducerProperties = new Properties();

       kafkaAvroProducerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
       kafkaAvroProducerProperties.setProperty (ProducerConfig.ACKS_CONFIG, "all");
       kafkaAvroProducerProperties.setProperty (ProducerConfig.RETRIES_CONFIG, "10"); //need not be set
       kafkaAvroProducerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
       kafkaAvroProducerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaAvroSerializer.class.getName());
       kafkaAvroProducerProperties.setProperty("schema.registry.url", "http://localhost:8081");

        KafkaProducer<String, CustomerV1> customerV1KafkaProducer = new KafkaProducer<String, CustomerV1>(kafkaAvroProducerProperties);

        CustomerV1 customerV1 = CustomerV1.newBuilder()
                .setFirstName("John")
                .setAge(26)
                .setAutomatedEmail(false)
                .setHeight(185.5f)
                .setLastName("Doe")
                .setWeight(85.6f)
                .build();

        ProducerRecord<String, CustomerV1> customerV1ProducerRecord =
                new ProducerRecord<String, CustomerV1>(CUSTOMER_AVRO_TOPIC_NAME, MESSAGE_KEY, customerV1);

        customerV1KafkaProducer.send(customerV1ProducerRecord, new Callback() {

            @Override
            public void onCompletion(RecordMetadata metadata, Exception exception) {

                if (exception == null) {
                    System.out.println("send is successful");
                    System.out.println(metadata.topic());
                } else {
                    System.err.println("There was an exception while sending message to the customer Topic");
                    exception.printStackTrace(System.err);
                }
            }
        });

        customerV1KafkaProducer.flush();
        customerV1KafkaProducer.close();
    }
}
