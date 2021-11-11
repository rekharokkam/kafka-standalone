package com.learning.spring.kafka.consumeradvancedconfig;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;

public class ConsumerHelper {

    private static final String ELASTIC_HOST_NAME = "kafka-standalone-cou-1278981902.us-west-2.bonsaisearch.net";
    private static final String ELASTIC_USER_NAME = "3h2xCUtAjM";
    private static final String ELASTIC_USER_PASSWORD = "Hk2sj7fyEwJt5mqLU8n";

    public static RestHighLevelClient createElasticClient () {

        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(ELASTIC_USER_NAME, ELASTIC_USER_PASSWORD));

        RestClientBuilder restClientBuilder = RestClient.builder(
                new HttpHost(ELASTIC_HOST_NAME, 443, "https"))
                .setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                    @Override
                    public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                        return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    }
                });

        RestHighLevelClient client = new RestHighLevelClient(restClientBuilder);
        return client;
    }

}
