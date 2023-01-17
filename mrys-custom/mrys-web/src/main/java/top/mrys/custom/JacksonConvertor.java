package top.mrys.custom;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import top.mrys.core.JsonConvertor;

/**
 * @author mrys
 */
public class JacksonConvertor implements JsonConvertor {

  @Override
  @SneakyThrows
  public String toJson(Object obj) {
    ObjectMapper mapper = SpringTool.getBean(ObjectMapper.class);
    return mapper.writeValueAsString(obj);
  }
}
