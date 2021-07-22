package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec03NumberTest extends BaseTest {

    @Test
    void keyValueIncreaseTest() {

        //given
        long startingVisits = 23;

        RAtomicLongReactive clientAtomicLong = client.getAtomicLong("user:1:visit");

        Mono<Void> set = clientAtomicLong.set(startingVisits);

        Mono<Long> get = clientAtomicLong.get()
                .doOnNext(this::logVisits);

        Mono<Long> incr = clientAtomicLong
                .incrementAndGet()
                .doOnNext(this::logVisits);

        //when
        Mono<Long> pipeline = set.then(get).then(incr);

        //then
        StepVerifier
                .create(pipeline)
                .expectNext(startingVisits + 1)
                .verifyComplete();
    }

    @Test
    void keyValueMultipleIncreaseTest() {

        //given
        long startingVisits = 23;

        RAtomicLongReactive clientAtomicLong = client.getAtomicLong("user:2:visit");

        Mono<Void> set = clientAtomicLong.set(startingVisits);

        Flux<Long> incrFlux = Flux.range(1, 30)
                .delayElements(Duration.ofMillis(100))
                .flatMap(i -> clientAtomicLong.incrementAndGet())
                .doOnNext(this::logVisits);

        Mono<Long> get = clientAtomicLong.get()
                .doOnNext(this::logVisits);

        //when
        Mono<Long> pipeline = set
                .thenMany(incrFlux)
                .then(get);

        //then
        Duration duration = StepVerifier
                .create(pipeline)
                .expectNext(startingVisits + 30)
                .verifyComplete();

        assertThat(duration).isCloseTo(Duration.ofSeconds(3), Duration.ofMillis(500));
    }

    private void logVisits(Long visits) {
        log.info("Student's visits: {}", visits);
    }

}
