package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec01KeyValueTest extends BaseTest {

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
}
