package com.learning.spring.kafka;


import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Properties;

@Slf4j
public class WordCountApp {

    private static final String BOOTSTRAP_SERVER = "localhost:9092";
    private static final String APPLICATION_ID_CONFIG ="wordcount-application";
    private static final String INBOUND_TOPIC_NAME = "wordcount-input";
    private static final String OUTBOUND_TOPIC_NAME = "wordcount-output";
    private static final String MESSAGE_KEY = "test_order";

    private static final Serde<String> stringSerde = Serdes.String();
    private static final Serde<Long> longSerde = Serdes.Long();

    public static void sendMessage() {
        Properties kafkaStreamsConfig = new Properties();

        kafkaStreamsConfig.put(StreamsConfig.APPLICATION_ID_CONFIG, APPLICATION_ID_CONFIG);
        kafkaStreamsConfig.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVER);
        kafkaStreamsConfig.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        kafkaStreamsConfig.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        kafkaStreamsConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> wordCountInput = builder.stream(INBOUND_TOPIC_NAME, Consumed.with(stringSerde, stringSerde));

        KTable<String, Long> wordCounts = wordCountInput
                .mapValues(textLine -> textLine.toLowerCase())
                .flatMapValues(lowercasedTextLine -> Arrays.asList(lowercasedTextLine.split(" ")))
                .selectKey((ignoredKey, word) -> word)
                .groupByKey()
                .count();
        wordCounts.toStream().to(OUTBOUND_TOPIC_NAME, Produced.with(stringSerde, longSerde));

        final Topology topology = builder.build();

        final KafkaStreams kafkaStreams = new KafkaStreams(topology, kafkaStreamsConfig);
        kafkaStreams.start();

//        Printing Streams Topology
//        log.info ("streams topology: {}", kafkaStreams.toString());

//        Graceful Shutdown of KafkaStream
        Runtime.getRuntime().addShutdownHook(new Thread(kafkaStreams::close));
    }

    public static void main(String[] args) {
        sendMessage ();
    }
}
