package com.learning.spring.kafka.consumeradvancedconfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import static com.learning.spring.kafka.consumeradvancedconfig.ConsumerHelper.createElasticClient;
import static com.learning.spring.kafka.consumeradvancedconfig.ConsumerHelper.extractJsonAttributeValue;

@Slf4j
public class KafkaTweetsConsumerElasticSearchPublisher {

    private static final String TOPIC_NAME = "twitter-tweets";
    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String GROUP_ID = "tweets-consumer-group";
    private static final String OFFRESET_RESET_CONFIG = "earliest";
    private static final String ENABLE_AUTO_COMMIT = "false";

    private Properties kafkaConsumerProperties = new Properties();
    private RestHighLevelClient restHighLevelClient;
    private KafkaConsumer<String, String> kafkaConsumer;

    public KafkaTweetsConsumerElasticSearchPublisher () {
        if (null == restHighLevelClient) {
            restHighLevelClient = createElasticClient();
        }
        consumerSetup();
    }

    private void  consumerSetup () {
        kafkaConsumerProperties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaConsumerProperties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        kafkaConsumerProperties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        kafkaConsumerProperties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, OFFRESET_RESET_CONFIG);
        kafkaConsumerProperties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, ENABLE_AUTO_COMMIT);

        kafkaConsumer = new KafkaConsumer<String, String>(kafkaConsumerProperties);
        kafkaConsumer.subscribe(Arrays.asList(TOPIC_NAME)); // can subscribe to more than one topic
    }

    public void consumeMessage () {

        while (true) {
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String, String> consumerRecord : consumerRecords) {
                String consumerMessage = consumerRecord.value();
                log.info("Topic : " + consumerRecord.topic());
                log.info("Key : {} :: value : " , consumerRecord.key() , consumerMessage);
                log.info("Partition : {} :: offset  : {}", consumerRecord.partition(), consumerRecord.offset() + "\n");

                //This is extracted for making an insert into elastic search idempotenet
                String tweetId = extractJsonAttributeValue ("id_str", consumerMessage);
                log.info("id_str of a tweet is : {}", tweetId);

                //this is where we insert data into elastic search
                IndexRequest indexRequest = new IndexRequest(
                        "twitter",
                        "tweets",
                        tweetId
                ).source(consumerMessage, XContentType.JSON);

                try {
                    IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
                    String indexId = indexResponse.getId();
                    log.info ("newly inserted Elastic Search document : {}", indexId);

                    //sleep for a sec to watch the operation slowly
                    Thread.sleep(1000);

                } catch (IOException | InterruptedException anyException) {
                    log.error("An exception occurred while sending the document to elastic search", anyException);
                }
            }
        }
    }

    public static void main(String[] args) {

        KafkaTweetsConsumerElasticSearchPublisher kafkaTweetsConsumerElasticSearchPublisher =
                new KafkaTweetsConsumerElasticSearchPublisher ();

        kafkaTweetsConsumerElasticSearchPublisher.consumeMessage();

        Runtime.getRuntime().addShutdownHook(new Thread (() -> {
            log.info("Closing Elastic Search client connection");
            try {
                kafkaTweetsConsumerElasticSearchPublisher.restHighLevelClient.close();
            } catch (IOException ioException) {
                log.error("IOException occurred while closing the elastic search connection", ioException);
            }
            log.info("We are done");

        }));
    }
}
