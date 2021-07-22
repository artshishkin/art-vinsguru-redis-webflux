package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.ExpiredObjectListener;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

@Slf4j
public class Lec05EventListenerTest extends BaseTest {

    @Test
    void expiredEventTest() {

        //given
        RBucketReactive<String> bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        String userName = FAKER.name().firstName();
        AtomicBoolean keyIsExpired = new AtomicBoolean(false);

        //when
        Mono<Void> set = bucket.set(userName, 700, TimeUnit.MILLISECONDS);
        Mono<String> get = bucket.get()
                .doOnNext(name -> log.info("User name: {}", name))
                .doFinally(signalType -> log.info("Finally: {}", signalType));

        Mono<Void> event = bucket
                .addListener((ExpiredObjectListener) name -> {
                    log.info("Expired key: {}", name);
                    keyIsExpired.set(true);
                })
                .then();

        Mono<Long> getTtl = bucket
                .remainTimeToLive()
                .doOnNext(ttl -> log.info("Remaining TTL: {} ms", ttl))
                .doOnNext(ttl -> assertThat(ttl).isCloseTo(300, withPercentage(30L)));

        Mono<String> pipeline = set
                .concatWith(event)

                .then(Mono.delay(ofMillis(200)))
                .then(get)
                .then(Mono.delay(ofMillis(200)))
                .then(get)

                .then(getTtl)

                .then(Mono.delay(ofMillis(400)))
                .then(get);

        //then
        Duration completeDuration = StepVerifier
                .create(pipeline)
                .verifyComplete();

        assertThat(completeDuration).isCloseTo(ofMillis(800), ofMillis(300));
        assertThat(keyIsExpired).isTrue();
    }
}
