package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

@Slf4j
public class Lec04BucketAsMapTest extends BaseTest {

    @Test
    void bucketsAsMap() {

        //given
        Mono<Void> createUserNames = Flux
                .range(1, 3)
                .map(i -> String.format("user:%d:name", i))
                .map(bucketName -> client.getBucket(bucketName, StringCodec.INSTANCE))
                .flatMap(objectRBucketReactive -> objectRBucketReactive.set(FAKER.name().name()))
                .then();

        //when
        Mono<Map<String, Object>> getMap = client.getBuckets(StringCodec.INSTANCE)
                .get("user:1:name", "user:2:name", "user:3:name", "user:4:name", "user:anotherNotPresent:name")
                .doOnNext(map -> log.info("Map: {}", map));

        //then
        StepVerifier.create(createUserNames.then(getMap))
                .expectNextMatches(map -> map.size() == 3)
                .verifyComplete();
    }
}
