package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.redisson.api.RListReactive;
import org.redisson.client.RedisException;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec09ListQueueStackTest extends BaseTest {

    @Nested
    class ListTest {

        // lrange number-input 0 -1

        private RListReactive<Long> numbers;

        @BeforeEach
        void setUp() {
            numbers = client.getList("number-input", LongCodec.INSTANCE);
            numbers.delete().subscribe(
                    res -> log.info("Deletion Result: {}", res),
                    error -> log.error("Error", error),
                    () -> log.info("Deletion complete")
            );
//            StepVerifier
//                    .create(numbers.delete())
//                    .expectNextCount(1) //no matter true or false
//                    .verifyComplete();
        }

        @Test
        void listTest_noOrderGuaranteed() {

            //when
            Mono<Long> listAdd = Flux.range(1, 10)
                    .map(Long::valueOf)
                    .doOnNext(num -> log.info("Adding {}", num))
                    .flatMap(numbers::add) //10 Publishers in different threads of thread pool
                    .doOnNext(res -> log.info("Added successfully: {}", res))
                    .filter(r -> r)
                    .count();

            //then
            StepVerifier
                    .create(listAdd)
                    .expectNext(10L)
                    .verifyComplete();
            StepVerifier
                    .create(numbers.size())
                    .expectNext(10)
                    .verifyComplete();
        }

        @Test
        void listTest_ordered_addFlux() {

            //when
            Flux<Long> listAdd = Flux.range(1, 10)
                    .map(Long::valueOf);

            Mono<Boolean> listAddAll = numbers.addAll(listAdd);

            //then
            StepVerifier
                    .create(listAddAll)
                    .expectNext(true)
                    .verifyComplete();
            StepVerifier
                    .create(numbers.size())
                    .expectNext(10)
                    .verifyComplete();
        }

        @Test
        void listTest_ordered_addCollection() {

            //when
            List<Long> longList = LongStream.rangeClosed(1, 10)
                    .boxed()
                    .collect(Collectors.toList());

            Mono<Boolean> listAddAll = numbers.addAll(longList);

            //then
            StepVerifier
                    .create(listAddAll)
                    .expectNext(true)
                    .verifyComplete();
            StepVerifier
                    .create(numbers.size())
                    .expectNext(10)
                    .verifyComplete();
        }

        @Test
        void listTest_ordered_fromIndex() {

            //given
            int startingIndex = 0;

            //when
            List<Long> longList = LongStream.rangeClosed(1, 10)
                    .boxed()
                    .collect(Collectors.toList());

            Mono<Boolean> listAddAll = numbers.addAll(startingIndex, longList);

            //then
            StepVerifier
                    .create(listAddAll)
                    .expectNext(true)
                    .verifyComplete();
            StepVerifier
                    .create(numbers.size())
                    .expectNext(10)
                    .verifyComplete();
        }

        @Test
        void listTest_ordered_wrongStartingIndex() {

            //given
            int startingIndex = 666;

            //when
            List<Long> longList = LongStream.rangeClosed(1, 10)
                    .boxed()
                    .collect(Collectors.toList());

            Mono<Boolean> listAddAll = numbers.addAll(startingIndex, longList);

            //then
            StepVerifier
                    .create(listAddAll)
                    .verifyErrorSatisfies(thr -> assertThat(thr)
                            .isInstanceOf(RedisException.class)
                            .hasMessageContaining("index: 666 but current size: 0")
                    );
        }
    }
}
