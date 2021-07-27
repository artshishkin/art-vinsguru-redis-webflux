package net.shyshkin.study.redis.redisspring.city.client;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.city.dto.City;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class CityClientTest {

    @Autowired
    CityClient cityClient;

    @Test
    void getCityInfo() {

        //when
        Mono<City> cityInfo = cityClient
                .getCityInfo("00603")
                .doOnNext(city -> log.debug("Retrieved city: {}", city));

        //then
        Duration duration = StepVerifier.create(cityInfo)
                .thenConsumeWhile(
                        city -> true,
                        city -> assertThat(city)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("zipcode", "00603")
                                .hasFieldOrPropertyWithValue("city", "Aguadilla")
                                .hasFieldOrPropertyWithValue("stateName", "Puerto Rico")
                )
                .verifyComplete();

        log.debug("Duration of city info call: {}", duration);
    }
}