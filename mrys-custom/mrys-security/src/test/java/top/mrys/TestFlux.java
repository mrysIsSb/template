package top.mrys;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * @author mrys
 * @date 2022/12/16 15:35
 */
@SpringBootTest(properties = "spring.main.web-application-type=reactive",webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class TestFlux {

    @Test
    public void test1(@Autowired WebTestClient webClient) {
        webClient
                .get().uri("/")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class).isEqualTo("Hello World");
    }
}
