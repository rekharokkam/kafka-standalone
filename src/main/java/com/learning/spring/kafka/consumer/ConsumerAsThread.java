package com.learning.spring.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConsumerAsThread {

    private Logger logger = LoggerFactory.getLogger(ConsumerAsThread.class);

    public void runConsumerAsThread () {

        //Latch for handling Threading
        CountDownLatch latch = new CountDownLatch(1);

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Runnable kafkaConsumerRunnable = new ConsumerThread(latch);

        //creating the runnable consumerunnable and starting the listening process.
        executorService.execute(kafkaConsumerRunnable);

        //add shutdown hook.
        Runtime.getRuntime().addShutdownHook(new Thread( () ->
            {
                logger.info("Caught Shutdown Hook");
                ( (ConsumerThread)kafkaConsumerRunnable).shutDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    //do nothing
                } finally {
                    logger.info("Application has exited");
                }
            }));

        try {
            latch.await();

        } catch (InterruptedException e) {
            logger.error("Application got Interrupted", e);
        } finally {
            logger.info("Application (Main Method) is closing");
        }
    }

    public static void main(String[] args) {
        new ConsumerAsThread ().runConsumerAsThread();
    }
}

class ConsumerThread implements Runnable{

    private static final String TOPIC_NAME = "first-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String GROUP_ID = "first-java-consumer-group";
    private static final String OFFRESET_RESET_CONFIG = "earliest";

    private Logger logger = LoggerFactory.getLogger(ConsumerThread.class);

    private Properties kafkaConsumerProperties = new Properties();
    private CountDownLatch latch;
    private KafkaConsumer<String, String> kafkaConsumer;

    public ConsumerThread (CountDownLatch latch) {

        kafkaConsumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaConsumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        kafkaConsumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFRESET_RESET_CONFIG);

        kafkaConsumer = new KafkaConsumer<String, String>(kafkaConsumerProperties);
        this.latch = latch;
    }

    private void consumeMessage () {

        kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME)); // can subscribe to more than one topic

        try {
            while (true) {
                ConsumerRecords<String, String> messages = kafkaConsumer.poll(Duration.ofMillis(100));

                for (ConsumerRecord<String, String> message : messages) {
                    logger.info("Topic : " + message.topic());
                    logger.info("Key : " + message.key() + ", Value : " + message.value());
                    logger.info("Partition : " + message.partition() + " , Offset : " + message.offset() + "\n");
                }
            }
        } catch (WakeupException wakeupException) {
            logger.info("Received shutdown signal");
        } finally {
            kafkaConsumer.close();
            //tell the main code that consumer is done consuming
            latch.countDown();
        }

    }

    @Override
    public void run() {
        consumeMessage ();
    }

    public void shutDown () {
        //This method is thread-safe and is useful in particular to abort a long poll
        kafkaConsumer.wakeup();
    }
}
