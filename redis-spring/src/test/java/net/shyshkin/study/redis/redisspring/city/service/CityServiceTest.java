package net.shyshkin.study.redis.redisspring.city.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.city.dto.City;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
@TestPropertySource(
        properties = {
                "app.city.info.ttl.value=2"
        }
)
class CityServiceTest {

    @Autowired
    CityService cityService;

    @Test
    void getCityInfo() throws InterruptedException {
        //given
        String zipcode = "00603";

        //when
        Duration duration = externalServiceCall(zipcode);
        log.debug("First call of external service took {}", duration);

        //then
        duration = externalServiceCall(zipcode);

        log.debug("Second call of external service took {} (retrieve cached value)", duration);
        assertThat(duration).isLessThan(Duration.ofMillis(200));

        //wait expiration
        Thread.sleep(2100);
        duration = externalServiceCall(zipcode);

        log.debug("Third call of external service took {} (after cache has been expired)", duration);
        assertThat(duration).isGreaterThan(Duration.ofSeconds(1));

        //wait for redisson__timeout__set:{city:info} in Redis server to be cleared by Redisson
        Thread.sleep(5000);
    }

    private Duration externalServiceCall(String zipcode) {
        Mono<City> cityInfo = cityService
                .getCityInfo(zipcode)
                .doOnNext(city -> log.debug("Retrieved city from external service: {}", city));

        return StepVerifier.create(cityInfo)
                .thenConsumeWhile(
                        city -> true,
                        city -> assertThat(city)
                                .hasNoNullFieldsOrProperties()
                                .hasFieldOrPropertyWithValue("zipcode", zipcode)
                                .hasFieldOrPropertyWithValue("city", "Aguadilla")
                                .hasFieldOrPropertyWithValue("stateName", "Puerto Rico")
                )
                .verifyComplete();
    }
}