package top.mrys;

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
}
