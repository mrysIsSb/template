package top.mrys.custom;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOptions;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregation;
import co.elastic.clients.elasticsearch._types.aggregations.TermsAggregation;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.SimpleJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.boot.autoconfigure.elasticsearch.ElasticsearchRestClientProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(ElasticsearchRestClientProperties.class)
public class AutoConfigurationEs {

  @Bean(destroyMethod = "close")
  public RestClient restClient(ElasticsearchRestClientProperties properties) {
    RestClientBuilder builder = RestClient.builder(properties.getUris().stream()
      .map(HttpHost::create)
      .toArray(HttpHost[]::new));
    builder.setRequestConfigCallback(requestConfigBuilder -> {
      requestConfigBuilder.setConnectTimeout((int) properties.getConnectionTimeout().toMillis());
      requestConfigBuilder.setSocketTimeout((int) properties.getReadTimeout().toMillis());
      return requestConfigBuilder;
    });
    builder.setHttpClientConfigCallback(httpClientBuilder -> {
      Credentials credentials = new UsernamePasswordCredentials(properties.getUsername(),
        properties.getPassword());
      BasicCredentialsProvider provider = new BasicCredentialsProvider();
      provider.setCredentials(AuthScope.ANY, credentials);
      httpClientBuilder.setDefaultCredentialsProvider(provider);
      return httpClientBuilder;
    });
    return builder.build();
  }

  @Bean
  public EsTemplate esTemplate(RestClient restClient) {
    return new DefaultEsTemplate(restClient);
  }
}
