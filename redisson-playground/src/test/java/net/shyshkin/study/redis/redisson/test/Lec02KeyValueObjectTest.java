package net.shyshkin.study.redis.redisson.test;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.dto.Student;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec02KeyValueObjectTest extends BaseTest {

    private static final Faker FAKER = Faker.instance();

    @Test
    void keyValueObjectTest() {

        //given
        var student = Student.builder()
                .name(FAKER.name().name())
                .age(FAKER.random().nextInt(18, 23))
                .city(FAKER.address().city())
                .mark(5)
                .mark(6)
                .mark(12)
                .build();

        RBucketReactive<Student> bucket = client.getBucket("student:1", new TypedJsonJacksonCodec(Student.class));

        //when
        Mono<Void> set = bucket.set(student);
        Mono<Student> get = bucket.get()
                .doOnNext(st -> log.info("Student: {}", st));

        //then
        StepVerifier
                .create(set.then(get))
                .expectNext(student)
                .verifyComplete();
    }

}
