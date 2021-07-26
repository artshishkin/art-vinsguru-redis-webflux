package net.shyshkin.study.redis.redisspring.fib.controller;

import net.shyshkin.study.redis.redisspring.fib.service.FibService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebFluxTest(FibController.class)
class FibControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    FibService fibService;

    @Test
    void getFibTest() {

        //given
        given(fibService.getFib(anyInt(), anyString())).willReturn(6765L);

        //when
        webTestClient.get().uri("/fib/20/art")
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(Long.TYPE)
                .isEqualTo(6765L);

        then(fibService).should().getFib(eq(20), eq("art"));
    }

    @Test
    void clearCacheTest() {

        //when
        webTestClient.get().uri("/fib/20/clear")
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();

        then(fibService).should().clearCache(eq(20));
    }
}