package com.learning.spring.kafka.kafkajavaprogramming101.producer;

import com.learning.spring.kafka.kafkajavaprogramming101.producercallback.MyProducerCallback;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class BasicProducerWithCallback {

    private static final String TOPIC_NAME = "first-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String MESSAGE_WITH_CALLBACK = "message with callback";

    private Properties kafkaProducerProperties = new Properties();
    private Logger logger = LoggerFactory.getLogger(BasicProducerWithCallback.class);

    public BasicProducerWithCallback () {
        kafkaProducerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaProducerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    private void sendMessageAndAttachCallback () {
        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(kafkaProducerProperties);

        for (int i = 0; i < 10; i ++) {
            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(TOPIC_NAME, i + " : " + MESSAGE_WITH_CALLBACK);

            //asynchronous
            kafkaProducer.send(producerRecord, new MyProducerCallback());
        }

        //flush data.
        //Flush actually causes queued records to be sent and blocks until they are completed (successfully or otherwise).
        // Calling get() immediately doesn't trigger anything to happen
        kafkaProducer.flush();
        //flush and close the topic
        kafkaProducer.close();
    }

    public static void main(String[] args) {
        new BasicProducerWithCallback().sendMessageAndAttachCallback ();
    }
}
