package com.learning.spring.kafka.consumeradvancedconfig;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;

import java.io.IOException;

import static com.learning.spring.kafka.consumeradvancedconfig.ConsumerHelper.createElasticClient;

@Slf4j
public class ElasticSearchPublisher {

    public static void main(String[] args) {
        String indexString = "{\"foo\": \"bar\"}";

        RestHighLevelClient restHighLevelClient = createElasticClient();

        IndexRequest indexRequest = new IndexRequest(
                "twitter",
                "tweets"
        ).source(indexString, XContentType.JSON);

        try {
            IndexResponse indexResponse = restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);

            String indexId = indexResponse.getId();
            log.info ("newly inserted Elastic Search document : {}", indexId);

        } catch (IOException ioException) {
            log.error("An exception occurred while sending the document to elastic search", ioException);
        } finally {
            try {
                restHighLevelClient.close();
            } catch (IOException ioException) {
                log.error("Exception occured while closing the elastic client", ioException);
            }
        }
    }
}
