package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RMapReactive;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

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

    @Test
    void mapTest_objectSave() {

        //given
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapReactive<Integer, Student> map = client.getMap("users", codec);

        //when
        Map<Integer, Student> studentMap = IntStream
                .rangeClosed(1, 3)
                .boxed()
                .collect(Collectors.toMap(Function.identity(), i -> generateRandomStudent()));

        Mono<Void> putAll = map.putAll(studentMap);

        Mono<Map<Integer, Student>> getAll = map.getAll(Set.of(1, 2, 3, 4, 5, 6, 7));

        //then
        StepVerifier
                .create(putAll.then(getAll))
                .assertNext(
                        resultMap -> assertThat(resultMap)
                                .satisfies(resMap -> log.info("Retrieved map: {}", resMap))
                                .hasSize(3)
                                .allSatisfy((idx, student) -> assertThat(student).hasNoNullFieldsOrProperties())
                )
                .verifyComplete();
    }

    private Student generateRandomStudent() {
        return Student.builder()
                .name(FAKER.name().firstName())
                .city(FAKER.address().city())
                .age(FAKER.random().nextInt(18, 45))
                .mark(FAKER.random().nextInt(100))
                .mark(FAKER.random().nextInt(100))
                .mark(FAKER.random().nextInt(100))
                .build();
    }
}
