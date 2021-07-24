package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.Test;
import org.redisson.api.RHyperLogLogReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec11HyperLogLogTest extends BaseTest {

    @Test
    void count() {
        //given
        RHyperLogLogReactive<Long> counter = this.client.getHyperLogLog("users:visits", LongCodec.INSTANCE);

        var visits1 = LongStream.rangeClosed(1, 25_000).boxed().collect(Collectors.toList());
        var visits2 = LongStream.rangeClosed(25_000, 50_000).boxed().collect(Collectors.toList());
        var visits3 = LongStream.rangeClosed(50_000, 70_000).boxed().collect(Collectors.toList());
        var visits4 = LongStream.rangeClosed(10_000, 90_000).boxed().collect(Collectors.toList());
        var visits5 = LongStream.rangeClosed(30_000, 100_000).boxed().collect(Collectors.toList());

        //when
        Mono<Long> successInsertions = Flux.just(visits1, visits2, visits3, visits4, visits5)
                .flatMap(counter::addAll)
                .filter(ok -> ok)
                .count();

        //then
        StepVerifier.create(successInsertions)
                .expectNextCount(1)
                .verifyComplete();

        Mono<Long> countMono = counter.count().doOnNext(c -> log.info("Estimated count: {}", c));
        StepVerifier.create(countMono)
                .assertNext(
                        estimatedCount -> assertThat(estimatedCount)
                                .isCloseTo(100_000L, Percentage.withPercentage(1)))
                .verifyComplete();

    }
}
