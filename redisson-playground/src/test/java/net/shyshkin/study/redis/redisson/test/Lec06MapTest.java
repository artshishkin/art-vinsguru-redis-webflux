package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;

@Slf4j
public class Lec06MapTest extends BaseTest {

    @Test
    void mapTest_1By1() {

        //given
        RMapReactive<String, String> map = client.getMap("user:1", StringCodec.INSTANCE);

        //when
        Mono<String> name = map.put("name", FAKER.name().firstName());
        Mono<String> age = map.put("age", FAKER.random().nextInt(18, 45).toString());
        Mono<String> city = map.put("city", FAKER.address().city());

        Flux<String> save = name.concatWith(age).concatWith(city);

        //then
        StepVerifier
                .create(save.then())
                .verifyComplete();
    }

    @Test
    void mapTest_fromJavaMap() {

        //given
        RMapReactive<String, String> map = client.getMap("user:2", StringCodec.INSTANCE);

        //when
        Map<String, String> javaMap = Map.of(
                "name", FAKER.name().firstName(),
                "age", FAKER.random().nextInt(18, 45).toString(),
                "city", FAKER.address().city()
        );
        Mono<Void> putAll = map.putAll(javaMap);

        //then
        StepVerifier
                .create(putAll)
                .verifyComplete();
    }
}
