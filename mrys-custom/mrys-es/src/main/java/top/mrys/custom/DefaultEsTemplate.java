package top.mrys.custom;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.SneakyThrows;
import org.apache.http.HttpEntity;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;

public class DefaultEsTemplate implements EsTemplate {

  private final RestClient restClient;

  public DefaultEsTemplate(RestClient restClient) {
    this.restClient = restClient;
  }


  @SneakyThrows
  @Override
  public <T> T save(T t) {
    Request request = new Request("POST", getSaveUrl(t));
    request.setJsonEntity(JSONUtil.toJsonStr(t));
    Response response = restClient.performRequest(request);
    byte[] content = getResponseContent(response);
    String idValue = getIdValue(t);
    if (idValue == null) {
      String id = JSONUtil.parseObj(content).getStr("_id");
      setIdValue(t, id);
    }
    return t;
  }

  private <T> String getSaveUrl(T t) {
    Index index = DocTool.getIndex(t.getClass());
    if (index == null) {
      throw new RuntimeException("没有设置索引");
    }
    String url = "/" + index.value() + "/_doc";
    String idValue = getIdValue(t);
    if (idValue != null) {
      url += "/" + idValue;
    }
    return url;
  }

  @Override
  @SneakyThrows
  public <T> void deleteById(Serializable t, Class<T> clazz) {
    Request request = new Request("DELETE", getDeleteUrl(t, clazz));
    Response response = restClient.performRequest(request);
    checkResponse(response);
  }

  private <T> String getDeleteUrl(Serializable id, Class<T> clazz) {
    Index index = DocTool.getIndex(clazz);
    if (index == null) {
      throw new RuntimeException("没有设置索引");
    }
    return "/" + index.value() + "/_doc/" + id;
  }

  @Override
  @SneakyThrows
  public <T> void deleteAll(Class<T> clazz) {
    Index index = DocTool.getIndex(clazz);
    if (index == null) {
      throw new RuntimeException("没有设置索引");
    }
    Request request = new Request("DELETE", "/" + index.value());
    Response response = restClient.performRequest(request);
    checkResponse(response);
  }

  @Override
  @SneakyThrows
  public <T> T updateById(T t) {
    String idValue = getIdValue(t);
    if (idValue == null) {
      throw new RuntimeException("id不能为空");
    }
    Request request = new Request("POST", "/" + getIndex(t).value() + "/_update/" + idValue);

    request.setJsonEntity(JSONUtil
      .createObj()
      .set("doc", t)
      .toString());
    Response response = restClient.performRequest(request);
    checkResponse(response);
    return t;
  }


  @SneakyThrows
  @Override
  public <T> T findById(Serializable id, Class<T> clazz) {
    Index index = DocTool.getIndex(clazz);
    if (index == null) {
      throw new RuntimeException("没有设置索引");
    }
    Request request = new Request("GET", "/" + index.value() + "/_doc/" + id + "?_source=true");
    Response response = restClient.performRequest(request);
    byte[] content = getResponseContent(response);
    return JSONUtil.parseObj(new String(content)).getBean("_source", clazz);
  }

  @Override
  @SneakyThrows
  public Object search(SearchOption searchOption, JSONObject body, Class<?> clazz) {
    Index index = DocTool.getIndex(clazz);
    if (index == null) {
      throw new RuntimeException("没有设置索引");
    }
    StringBuilder url = new StringBuilder("/" + index.value() + "/_search");
    if (searchOption.getFrom() != null) {
      url.append("?from=").append(searchOption.getFrom());
    }
    if (searchOption.getSize() != null) {
      url.append("&size=").append(searchOption.getSize());
    }
    if (searchOption.getSort() != null) {
      url.append("&sort=").append(searchOption.getSort());
    }
    if (searchOption.getExplain() != null) {
      url.append("&explain=").append(searchOption.getExplain());
    }
    if (searchOption.getPretty() != null && searchOption.getPretty()) {
      url.append("&pretty");
    }
//    if (searchOption.getLenient() != null) {
//      url.append("&lenient=").append(searchOption.getLenient());
//    }

    Request request = new Request("GET", url.toString());
    request.setJsonEntity(body.toString());
    Response response = restClient.performRequest(request);
    byte[] content = getResponseContent(response);
    return null;
  }

  /**
   * 获取id的值
   */
  protected <T> String getIdValue(T t) {
    Field idField = DocTool.getIdField(t.getClass());
    if (idField != null) {
      idField.setAccessible(true);
      try {
        Object o = idField.get(t);
        if (o != null) {
          return o.toString();
        }
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
    return null;
  }

  private static <T> Index getIndex(T t) {
    Index index = DocTool.getIndex(t.getClass());
    if (index == null) {
      throw new RuntimeException("没有设置索引");
    }
    return index;
  }

  /**
   * 设置id的值
   */
  protected <T> void setIdValue(T t, String id) {
    Field idField = DocTool.getIdField(t.getClass());
    if (idField != null) {
      idField.setAccessible(true);
      try {
        idField.set(t, id);
      } catch (IllegalAccessException e) {
        e.printStackTrace();
      }
    }
  }

  private static byte[] getResponseContent(Response response) {
    checkResponse(response);
    HttpEntity entity = response.getEntity();
    byte[] bytes = new byte[(int) entity.getContentLength()];
    try {
      entity.getContent().read(bytes);
      System.out.println(new String(bytes));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return bytes;
  }

  private static void checkResponse(Response response) {
    if (response.getStatusLine().getStatusCode() != 200
      && response.getStatusLine().getStatusCode() != 201) {
      throw new RuntimeException(Arrays.toString(response.getWarnings().toArray()));
    }
  }
}
