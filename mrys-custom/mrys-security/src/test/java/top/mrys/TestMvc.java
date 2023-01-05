package top.mrys;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

/**
 * @author mrys
 * @date 2022/12/13 10:05
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TestMvc {


  @Autowired
  private MockMvc mockMvc;

  @Test
  public void testGet() throws Exception {
    MockHttpServletRequestBuilder param = MockMvcRequestBuilders
      .get("/test/test1?name={name}", "mrys")
      .header("access-token", "test_access_token")
      .accept("application/json");

    mockMvc.perform(param)
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("test1mrys"))
      .andDo(MockMvcResultHandlers.print());
  }

  @Test
  public void testAnno() throws Exception {
    MockHttpServletRequestBuilder param = MockMvcRequestBuilders
      .get("/test/testAnno")
      .accept("application/json");

    mockMvc.perform(param)
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("testAnno"))
      .andDo(MockMvcResultHandlers.print());
  }

  @Test
  public void testAuth() throws Exception {
    MockHttpServletRequestBuilder param = MockMvcRequestBuilders
      .get("/test/testAuth")
      .accept("application/json");

    mockMvc.perform(param)
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("testAuth"))
      .andDo(MockMvcResultHandlers.print());
  }

  @Test
  public void testAuth2() throws Exception {
    MockHttpServletRequestBuilder param = MockMvcRequestBuilders
      .get("/test/testAuth2")
      .header("access-token", "test_access_token")
      .accept("application/json");

    mockMvc.perform(param)
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("testAuth2"))
      .andDo(MockMvcResultHandlers.print());
  }

  @Test
  public void testAuth3() throws Exception {
    MockHttpServletRequestBuilder param = MockMvcRequestBuilders
      .get("/test/testAuth3")
      .header("access-token", "test_access_token")
      .accept("application/json");

    mockMvc.perform(param)
      .andExpect(MockMvcResultMatchers.status().isOk())
      .andExpect(MockMvcResultMatchers.content().string("testAuth3"))
      .andDo(MockMvcResultHandlers.print());
  }


  @Test
  public void login() throws Exception {
    JSONObject body = JSONUtil.createObj()
            .set("username", "伊杰")
            .set("password", "123456");
    MockHttpServletRequestBuilder param = MockMvcRequestBuilders
            .post("/auth/login/{type}", "local")
            .contentType("application/json")
            .content(body.toString())
            .accept("application/json");

    mockMvc.perform(param)
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(0))
            .andDo(MockMvcResultHandlers.print());
  }

}
