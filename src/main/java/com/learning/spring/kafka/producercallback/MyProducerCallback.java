package com.learning.spring.kafka.producercallback;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyProducerCallback implements Callback {

    private Logger logger = LoggerFactory.getLogger(MyProducerCallback.class);

    public void onCompletion(RecordMetadata metadata, Exception exception) {
        if (exception == null) {
            logger.info("received new metadata for new message. \n" +
                    "Topic : " + metadata.topic() + "\n" +
                    "Partition : " + metadata.partition() + "\n" +
                    "Offset : " + metadata.offset() + "\n" +
                    "Timestamp : " + metadata.timestamp());
        } else {
            logger.error("Message could not be sent, there was an exception : ", exception);
        }
    }
}
