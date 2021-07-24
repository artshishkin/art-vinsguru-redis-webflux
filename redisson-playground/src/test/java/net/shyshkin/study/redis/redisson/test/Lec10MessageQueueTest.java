package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.subscriber.LogSubscriber;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBlockingDequeReactive;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
public class Lec10MessageQueueTest extends BaseTest {

    private RBlockingDequeReactive<Long> msgQueue;

    @BeforeAll
    void beforeAll() {
        msgQueue = client.getBlockingDeque("message-queue", LongCodec.INSTANCE);
    }

    @Test
    void consumer1() throws InterruptedException {

        //when
        msgQueue.takeElements()
                .subscribe(new LogSubscriber("Consumer 1"));

        //then
        Thread.sleep(600_000);

    }

    @Test
    void consumer2() {

        //when
        Flux<Long> takeElements = msgQueue.takeElements();

        //then
        StepVerifier.create(takeElements)
                .thenConsumeWhile(el -> true, el -> log.info("Consumer 2: {}", el))
                .verifyComplete();
    }

    @Test
    void producer() {
        //given
        Flux<Long> longFlux = Flux.range(1, 10)
                .delayElements(Duration.ofSeconds(1))
                .map(Long::valueOf)
                .doOnNext(el -> log.info("Pushed {}", el));

        //when
        Mono<Boolean> addAll = msgQueue.addAll(longFlux);

        //then
        StepVerifier.create(addAll)
                .expectNext(true)
                .verifyComplete();

    }
}
