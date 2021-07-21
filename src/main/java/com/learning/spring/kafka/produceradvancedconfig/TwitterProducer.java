package com.learning.spring.kafka.produceradvancedconfig;

import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterProducer {

    private static final String CONSUMER_KEY = "F7JikmUt6SkQHYiZXFbgVpTZ4";
    private static final String CONSUMER_SECRET = "4yKV8qO1ttlYX3E31BWTYqJpXYDoLQv3FBI7lpvYOTqp97cRUG";
    private static final String ACCESS_TOKEN = "2746149770-u6SdzYuuehtoIVKNUQYg6mMLQ5ybmnXHuOnOQst";
    private static final String ACCESS_TOKEN_SECRET = "6BQP5FYgYAchSITWuTYcvBKYjDc1Pa4apXHnOwgYODZNc";
    private static final List<String> TWEET_TERMS = Arrays.asList("kafka");

    private static final String TWITTER_TWEETS_TOPIC_NAME = "twitter-tweets-topic";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";

    private Logger logger = LoggerFactory.getLogger(TwitterProducer.class.getName());

    public Client createTwitterClient (BlockingQueue<String> msgQueue) {

        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        // Optional: set up some followings and track terms
        hosebirdEndpoint.trackTerms(TWEET_TERMS);

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(
                CONSUMER_KEY,
                CONSUMER_SECRET,
                ACCESS_TOKEN,
                ACCESS_TOKEN_SECRET);

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01") // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue));

        return builder.build();
    }

    public void run () {
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(1000);

        Client twitterClient = createTwitterClient(msgQueue);

        // Attempts to establish a connection.
        twitterClient.connect();

        //Create the Kafka Producer
        KafkaProducer<String, String> twitterTweetsKafkaProducer = createKafkaProducer();

        //Adding shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread (() -> {
            logger.info("Stopping Twitter read and Kafka populate Application");

            logger.info("Shutting down twitter client");
            twitterClient.stop();

            logger.info("Closing Kafka Producer");
            twitterTweetsKafkaProducer.close();

            logger.info("We are done");

        }));

        // on a different thread, or multiple different threads....
        while (!twitterClient.isDone()) {
            String msg = null;
            try {
                msg = msgQueue.poll(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
                twitterClient.stop();
            }

            if (null != msg) {
                logger.info(msg);
                twitterTweetsKafkaProducer.send(new ProducerRecord<>(TWITTER_TWEETS_TOPIC_NAME, null, msg), new Callback() {
                    @Override
                    public void onCompletion(RecordMetadata metadata, Exception exception) {

                        if (exception != null){
                            logger.error("Something went wrong while producing the message");
                            exception.printStackTrace();
                        }
                    }
                });
            }
        }

        logger.info("End of twitter client application");
    }

    public KafkaProducer<String, String> createKafkaProducer () {
        Properties kafkaProducerProperties = new Properties();

        kafkaProducerProperties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaProducerProperties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducerProperties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        //Safe Producer settings
        kafkaProducerProperties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true");
        kafkaProducerProperties.setProperty (ProducerConfig.ACKS_CONFIG, "all");
        kafkaProducerProperties.setProperty (ProducerConfig.RETRIES_CONFIG, String.valueOf(Integer.MAX_VALUE)); //need not be set
        kafkaProducerProperties.setProperty (ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5"); //nned not be set


        KafkaProducer <String, String> kafkaProducerWithKey = new KafkaProducer<>(kafkaProducerProperties);
        return kafkaProducerWithKey;
    }

    public static void main(String[] args) {
        new TwitterProducer ().run();
    }
}
