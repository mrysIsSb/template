package top.mrys.custom;


import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import lombok.SneakyThrows;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AutoConfigurationEs.class})
//@AutoConfigureMockMvc
public class TestEs {

  @Autowired
  private RestClient restClient;

  @Autowired
  private EsTemplate esTemplate;

  @Test
  public void test() {
    assert restClient != null;
  }

  @SneakyThrows
  @Test
  public void save() {
    Goods goods = new Goods();
    goods.setDesc("desc");
    goods.setBrand("brand");
    goods.setName("name");
    goods.setPrice(BigDecimal.ZERO);
    goods.setType("type");
//    goods.setId("goods_3");
    goods.setCreateTime(new Date());
    Goods save = esTemplate.save(goods);
    System.out.println(save);
  }

  @Test
  public void saveBatch() {
    for (int i = 0; i < 100; i++) {
      Goods goods = new Goods();
      goods.setDesc("desc");
      goods.setBrand("brand");
      goods.setName("name");
      goods.setPrice(BigDecimal.ZERO);
      goods.setType("type");
      goods.setId("goods_" + i);
      goods.setCreateTime(new Date());
      esTemplate.save(goods);
    }
  }

  @Test
  public void deleteById() {
    esTemplate.deleteById("12312312", Goods.class);
  }

  @Test
  public void deleteAll() {
    esTemplate.deleteAll(Goods.class);
  }

  @Test
  public void updateById() {
    Goods t = new Goods();
    t.setId("goods_1");
    t.setDesc("new desc");
    t.setName("new name");
    t.setPrice(BigDecimal.valueOf(100));
    esTemplate.updateById(t);
  }

  @Test
  public void findById() {
    Goods goods = esTemplate.findById("goods_1", Goods.class);
    System.out.println(goods);
  }

  @Test
  public void search() {
    SearchOption searchOption = new SearchOption();
    searchOption.setFrom(0);
    searchOption.setSize(10);
    searchOption.setPretty(true);
    searchOption.setSort("price:desc,createTime:desc");
    JSONObject query = JSONUtil.createObj();
    query.putByPath("query.match.name", "new name");
    Object search = esTemplate.search(searchOption,
      query, Goods.class);
    System.out.println(search);
  }

  @Test
  @SneakyThrows
  public void testHighLevelClient() {
    RestClient httpClient = RestClient.builder(
      new HttpHost("localhost", 9200)
    ).build();

// Create the Java API Client with the same low level client
    ElasticsearchTransport transport = new RestClientTransport(
      httpClient,
      new JacksonJsonpMapper()
    );


    ElasticsearchClient esClient = new ElasticsearchClient(transport);
    esClient.search(s -> s.index("goods").from(1).size(10)
      .sort(srt -> srt.field(fi -> fi.field("price").order(SortOrder.Desc)))
      .query(q -> q.match(m -> m.field("name").query("name"))), Goods.class);
  }
}
