package top.mrys.custom;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import co.elastic.clients.json.SimpleJsonpMapper;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.util.HashMap;

public class Test2 {
  public static void main(String[] args) throws IOException {
    // Create the low-level client
    RestClient restClient = RestClient.builder(
      new HttpHost("localhost", 9200)).build();

    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));


    ElasticsearchTransport transport = new RestClientTransport(
      restClient, new JacksonJsonpMapper(objectMapper));

    // And create the API client
    ElasticsearchClient client = new ElasticsearchClient(transport);

    SearchRequest request = new SearchRequest.Builder()
      .index("goods")
      .source(s->s.fetch(true))
//      .query(q -> q.bool(b ->
//        b.must(m -> m.term(t -> t.field("user").value("mrys")))
//          .must(m -> m.range(r -> r.field("age").gte(JsonData.of(10)).lte(JsonData.of(20))))
//          .must(m -> m.match(ma -> ma.field("fullText").query("mrys")))
//      ))
      .from(0)
      .size(10)
      .sort(s -> s.field(f -> f.field("createTime").order(SortOrder.Asc)))
      .aggregations("types", a -> a.terms(t -> t.field("type").size(10)))
      .build();
    SearchResponse<Goods> search = client.search(request, Goods.class);
    System.out.println(search.aggregations());
    System.out.println(search.hits().total());
    System.out.println(search.hits().hits().get(0).source());
  }
}
