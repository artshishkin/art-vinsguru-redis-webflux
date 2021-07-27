package net.shyshkin.study.redis.redisspring.city.service;

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
class CityServiceTest {

    @Autowired
    CityService cityService;

    @Test
    void getCityInfo() {
        //given
        String zipcode = "00603";

        //when
        Mono<City> cityInfo = cityService
                .getCityInfo(zipcode)
                .doOnNext(city -> log.debug("Retrieved city from external service: {}", city));

        Duration duration = StepVerifier.create(cityInfo)
                .thenConsumeWhile(
                        city -> true,
                        city -> assertThat(city)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("zipcode", zipcode)
                                .hasFieldOrPropertyWithValue("city", "Aguadilla")
                                .hasFieldOrPropertyWithValue("stateName", "Puerto Rico")
                )
                .verifyComplete();

        log.debug("First call of external service took {}", duration);

        //then
        duration = StepVerifier.create(cityInfo)
                .thenConsumeWhile(
                        city -> true,
                        city -> assertThat(city)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("zipcode", zipcode)
                                .hasFieldOrPropertyWithValue("city", "Aguadilla")
                                .hasFieldOrPropertyWithValue("stateName", "Puerto Rico")
                )
                .verifyComplete();

        log.debug("Second call of external service took {}", duration);
        assertThat(duration).isLessThan(Duration.ofMillis(200));
    }

    @Test
    void getCityInfoFast() {
        //given
        String zipcode = "00603";

        //when
        Mono<City> cityInfo = cityService
                .getCityInfoFast(zipcode)
                .doOnNext(city -> log.debug("Retrieved city from external service: {}", city));

        Duration duration = StepVerifier.create(cityInfo)
                .thenConsumeWhile(
                        city -> true,
                        city -> assertThat(city)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("zipcode", zipcode)
                                .hasFieldOrPropertyWithValue("city", "Aguadilla")
                                .hasFieldOrPropertyWithValue("stateName", "Puerto Rico")
                )
                .verifyComplete();

        log.debug("First call of external service took {}", duration);

        //then
        duration = StepVerifier.create(cityInfo)
                .thenConsumeWhile(
                        city -> true,
                        city -> assertThat(city)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("zipcode", zipcode)
                                .hasFieldOrPropertyWithValue("city", "Aguadilla")
                                .hasFieldOrPropertyWithValue("stateName", "Puerto Rico")
                )
                .verifyComplete();

        log.debug("Second call of external service took {}", duration);
        assertThat(duration).isLessThan(Duration.ofMillis(200));
    }
}