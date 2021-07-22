package net.shyshkin.study.redis.redisson.test;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofMillis;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Percentage.withPercentage;

@Slf4j
public class Lec01KeyValueTest extends BaseTest {

    private static final Faker FAKER = Faker.instance();

    @Test
    void keyValueAccessTest() {

        //given
        RBucketReactive<String> bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);

        //when
        Mono<Void> set = bucket.set("Art");
        Mono<String> get = bucket.get()
                .doOnNext(userName -> log.info("User name: {}", userName))
                .doOnNext(System.out::println);

        //then
        StepVerifier
                .create(set.then(get))
                .expectNext("Art")
                .verifyComplete();
    }

    @Test
    void keyValueExpiryTest() {

        //given
        RBucketReactive<String> bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        String name = FAKER.name().firstName();

        //when
        Mono<Void> set = bucket.set(name, 700, TimeUnit.MILLISECONDS);
        Mono<String> get = bucket.get()
                .doOnNext(userName -> log.info("User name: {}", userName))
                .doFinally(signalType -> log.info("Finally: {}", signalType));

        Mono<String> pipeline = set
                .then(Mono.delay(ofMillis(200)))
                .then(get)
                .then(Mono.delay(ofMillis(200)))
                .then(get)
                .then(Mono.delay(ofMillis(400)))
                .then(get);

        //then
        Duration completeDuration = StepVerifier
                .create(pipeline)
                .verifyComplete();

        assertThat(completeDuration).isCloseTo(ofMillis(800), ofMillis(150));
    }

    @Test
    void keyValueExtendExpiryTest() {

        //given
        RBucketReactive<String> bucket = client.getBucket("user:1:name", StringCodec.INSTANCE);
        String name = FAKER.name().firstName();

        //when
        Mono<Void> set = bucket.set(name, 700, TimeUnit.MILLISECONDS);
        Mono<String> get = bucket.get()
                .doOnNext(userName -> log.info("User name: {}", userName))
                .doFinally(signalType -> log.info("Finally: {}", signalType));

        Mono<Boolean> extendExpire = bucket
                .expire(10, TimeUnit.SECONDS)
                .doOnNext(result -> log.info("Updating expiration time: {}", result))
                .doOnNext(result -> assertThat(result).isTrue());

        Mono<Long> getTtl = bucket
                .remainTimeToLive()
                .doOnNext(ttl -> log.info("Remaining TTL: {} ms", ttl))
                .doOnNext(ttl -> assertThat(ttl).isCloseTo(10 * 1000, withPercentage(5L)));

        Mono<String> pipeline = set
                .then(Mono.delay(ofMillis(200)))
                .then(get)
                .then(Mono.delay(ofMillis(200)))
                .then(get)

                .then(extendExpire)
                .then(getTtl)

                .then(Mono.delay(ofMillis(400)))
                .then(get);

        //then
        Duration completeDuration = StepVerifier
                .create(pipeline)
                .expectNext(name)
                .verifyComplete();

        assertThat(completeDuration).isCloseTo(ofMillis(800), ofMillis(300));
    }
}
