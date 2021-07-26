package net.shyshkin.study.redis.redisspring;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.redisson.api.RAtomicLongReactive;
import org.redisson.api.RedissonReactiveClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

@Slf4j
@SpringBootTest
class RedisSpringApplicationTests {

    @Autowired
    ReactiveStringRedisTemplate template;

    @Autowired
    RedissonReactiveClient client;

    @Test
    void valueOperationsTest() {
        ReactiveValueOperations<String, String> valueOperations = template.opsForValue();

        Mono<Boolean> set = valueOperations.set("user:1:name", "Art");
        Mono<String> get = valueOperations.get("user:1:name");

        Mono<String> pipeline = set.then(get).doOnNext(name -> log.debug("Retrieved name: {}", name));

        StepVerifier.create(pipeline)
                .expectNext("Art")
                .verifyComplete();
    }

    @RepeatedTest(3)
    void manyNetworkCalls_RedisTest() {

        long before = System.currentTimeMillis();

        ReactiveValueOperations<String, String> valueOperations = template.opsForValue();
        Mono<Void> insertion = Flux.range(1, 500_000)
                .flatMap(i -> valueOperations.increment("user:1:visits")) //incr
                .then();

        Duration duration = StepVerifier.create(insertion)
                .verifyComplete();

        long after = System.currentTimeMillis();

        log.debug("Duration: {}", duration);
        log.debug("Time taken: {} ms", after - before);

    }

    @RepeatedTest(3)
    void manyNetworkCalls_RedissonTest() {

        long before = System.currentTimeMillis();

        RAtomicLongReactive atomicLong = client.getAtomicLong("user:1:visits");
        Mono<Void> insertion = Flux.range(1, 500_000)
                .flatMap(i -> atomicLong.incrementAndGet()) //incr
                .then();

        Duration duration = StepVerifier.create(insertion)
                .verifyComplete();

        long after = System.currentTimeMillis();

        log.debug("Duration: {}", duration);
        log.debug("Time taken: {} ms", after - before);

    }

}
