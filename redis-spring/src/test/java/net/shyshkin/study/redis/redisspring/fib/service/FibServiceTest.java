package net.shyshkin.study.redis.redisspring.fib.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class FibServiceTest {

    @Autowired
    FibService fibService;

    @ParameterizedTest
    @CsvSource({
            "30,832040",
            "45,1134903170",
            "47,2971215073"
    })
    void getFib(int index, long expectedResult) {

        //when
        Mono<Long> fibResult = Mono.fromSupplier(() -> fibService.getFib(index, "art"));

        //then
        Duration duration = StepVerifier.create(fibResult)
                .expectNext(expectedResult)
                .verifyComplete();

        log.debug("Fibonacci calculation for {} takes {}", index, duration);
    }

    @RepeatedTest(100)
    void getFib_cached() {

        //given
        int index = 45;

        //when
        Mono<Long> fibResult = Mono.fromSupplier(() -> fibService.getFib(index, "kate"));

        //then
        Duration duration = StepVerifier.create(fibResult)
                .expectNext(1134903170L)
                .verifyComplete();

        log.debug("Fibonacci calculation for {} takes {}", index, duration);
    }

    @ParameterizedTest
    @ValueSource(strings = {"art", "kate", "arina", "nazar"})
    void getFib_userAgnostic(String username) {

        //given
        int index = 45;

        //when
        Mono<Long> fibResult = Mono.fromSupplier(() -> fibService.getFib(index, username));

        //then
        Duration duration = StepVerifier.create(fibResult)
                .expectNext(1134903170L)
                .verifyComplete();

        log.debug("Fibonacci calculation for user {} and index {} takes {}", username, index, duration);
        if (!Objects.equals("art", username)) assertThat(duration).isLessThan(Duration.ofSeconds(1));
    }
}