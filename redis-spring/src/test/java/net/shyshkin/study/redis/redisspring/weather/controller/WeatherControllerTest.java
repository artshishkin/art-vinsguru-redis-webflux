package net.shyshkin.study.redis.redisspring.weather.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestPropertySource(properties = {
        "app.weather.update.period=2000"
})
class WeatherControllerTest {

    WebTestClient webTestClient;

    @Autowired
    WeatherController weatherController;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(weatherController).build();
    }

    @Test
    void getWeatherInfo() throws InterruptedException {
        //given
        //wait for scheduled update happened
        Thread.sleep(1200);

        //when
        EntityExchangeResult<Integer> exchangeResult = webTestClient.get().uri("/weather/3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.TYPE)
                .value(temperature -> assertThat(temperature).isNotEqualTo(0))
                .returnResult();

        Integer temperature1 = exchangeResult.getResponseBody();
        log.debug("Temperature 1: {}", temperature1);

        //Wait for another update
        Thread.sleep(2000);

        //then
        exchangeResult = webTestClient.get().uri("/weather/3")
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.TYPE)
                .value(temperature -> assertThat(temperature).isNotEqualTo(temperature1))
                .returnResult();

        Integer temperature2 = exchangeResult.getResponseBody();
        log.debug("Temperature 2: {}", temperature2);
    }
}