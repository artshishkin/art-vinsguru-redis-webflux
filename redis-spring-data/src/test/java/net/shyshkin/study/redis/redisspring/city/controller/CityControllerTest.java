package net.shyshkin.study.redis.redisspring.city.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.city.dto.City;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class CityControllerTest {

    @Autowired
    CityController cityController;

    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(cityController).build();
    }

    @Test
    void getCityInfo() {
        //given
        String zipcode = "00603";

        //when
        long firstCallDuration_ms = externalCityServiceCall(zipcode);
        log.debug("First call took {} ms", firstCallDuration_ms);

        long secondCallDuration_ms = externalCityServiceCall(zipcode);
        log.debug("Second call took {} ms", secondCallDuration_ms);

        //then
        assertThat(secondCallDuration_ms).isLessThan(200);
    }

    private long externalCityServiceCall(String zipcode) {

        long start = System.currentTimeMillis();

        webTestClient.get().uri("/city/{zipcode}", zipcode)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(City.class)
                .value(city -> assertThat(city)
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("zipcode", zipcode)
                        .hasFieldOrPropertyWithValue("city", "Aguadilla")
                        .hasFieldOrPropertyWithValue("stateName", "Puerto Rico")
                );
        return System.currentTimeMillis() - start;

    }
}