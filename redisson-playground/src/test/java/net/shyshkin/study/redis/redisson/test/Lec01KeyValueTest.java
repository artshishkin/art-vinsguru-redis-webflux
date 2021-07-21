package net.shyshkin.study.redis.redisson.test;

import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class Lec01KeyValueTest extends BaseTest {

    @Test
    void keyValueAccessTest() {

        //given
        RBucketReactive<String> bucket = client.getBucket("user:1:name");

        //when
        Mono<Void> set = bucket.set("Art");
        Mono<String> get = bucket.get()
                .doOnNext(System.out::println);

        //then
        StepVerifier
                .create(set.then(get))
                .expectNext("Art")
                .verifyComplete();
    }
}
