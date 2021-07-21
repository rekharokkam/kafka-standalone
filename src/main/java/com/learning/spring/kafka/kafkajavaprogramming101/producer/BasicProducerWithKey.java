package com.learning.spring.kafka.kafkajavaprogramming101.producer;

import com.learning.spring.kafka.kafkajavaprogramming101.producercallback.MyProducerCallback;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class BasicProducerWithKey {

    private static final String TOPIC_NAME = "first-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String MESSAGE_WITH_KEY = "message with Key";

    private Properties kafkaProducerProperties = new Properties();
    private Logger logger = LoggerFactory.getLogger(BasicProducerWithKey.class);


    public BasicProducerWithKey () {
        kafkaProducerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaProducerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    }

    private void sendMessageWithKey () throws ExecutionException, InterruptedException {
        KafkaProducer <String, String> kafkaProducerWithKey = new KafkaProducer<>(kafkaProducerProperties);

        for (int i = 0; i < 10; i ++) {
            String key = "id_" + i;
            logger.info("Message Key : " + key);

            ProducerRecord<String, String> producerRecord =
                    new ProducerRecord<>(TOPIC_NAME, key, i + " : " + MESSAGE_WITH_KEY);

            //asynchronous
            kafkaProducerWithKey.send(producerRecord, new MyProducerCallback()).
                    get(); //adding this here will block the .send() to make it synchronous - not recommended for real time
        }

        //flush data
        kafkaProducerWithKey.flush();
        //flush and close the topic
        kafkaProducerWithKey.close();
    }

    public static void main(String[] args) throws Exception{
        new BasicProducerWithKey ().sendMessageWithKey();
    }
}
