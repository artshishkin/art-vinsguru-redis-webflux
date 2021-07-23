package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.dto.Student;
import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.redisson.api.RMapCacheReactive;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec07MapCacheTest extends BaseTest {

    LogSubscriber subscriber = new LogSubscriber();

    @Test
    void mapCacheTest() {

        //given
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapCacheReactive<Integer, Student> mapCache = client.getMapCache("users:cache", codec);

        //when
        Mono<Student> put1 = mapCache.put(1, generateRandomStudent(), 100, TimeUnit.MILLISECONDS);
        Mono<Student> put2 = mapCache.put(2, generateRandomStudent(), 300, TimeUnit.MILLISECONDS);
        Mono<Student> put3 = mapCache.put(3, generateRandomStudent(), 500, TimeUnit.MILLISECONDS);

        Mono<Void> putAll = put1.concatWith(put2).concatWith(put3).then();

        Mono<Map<Integer, Student>> getAll = mapCache.getAll(Set.of(1, 2, 3, 4, 5, 6, 7));

        Mono<Map<Integer, Student>> pipeline = putAll
                .then(Mono.delay(Duration.ofMillis(400)))
                .then(getAll);

        //then
        StepVerifier
                .create(pipeline)
                .assertNext(
                        resultMap -> assertThat(resultMap)
                                .satisfies(resMap -> log.info("Retrieved map: {}", resMap))
                                .hasSize(1)
                                .allSatisfy((idx, student) -> assertThat(student).hasNoNullFieldsOrProperties())
                )
                .verifyComplete();
    }

    @Test
    void mapCacheTest_vinsguru() throws InterruptedException {

        //given
        TypedJsonJacksonCodec codec = new TypedJsonJacksonCodec(Integer.class, Student.class);
        RMapCacheReactive<Integer, Student> mapCache = client.getMapCache("users:cache", codec);

        //when
        Mono<Student> put1 = mapCache.put(1, generateRandomStudent(), 500, TimeUnit.MILLISECONDS);
        Mono<Student> put2 = mapCache.put(2, generateRandomStudent(), 700, TimeUnit.MILLISECONDS);
        Mono<Student> put3 = mapCache.put(3, generateRandomStudent(), 900, TimeUnit.MILLISECONDS);

        Mono<Void> putAll = put1.concatWith(put2).concatWith(put3).then();

        StepVerifier
                .create(putAll)
                .verifyComplete();

        mapCache.get(1).subscribe(new LogSubscriber());
        mapCache.get(2).subscribe(st -> log.info("Next: {}", st), ex -> log.error("Error", ex), () -> log.info("Completed"));
        mapCache.get(3).subscribe(new LogSubscriber());

        Thread.sleep(700);
        System.out.println();
        System.out.println("------------After a pause-------------");
        System.out.println();
        mapCache.get(1).subscribe(new LogSubscriber());
        mapCache.get(2).subscribe(new LogSubscriber());
        mapCache.get(3).subscribe(new LogSubscriber());

        StepVerifier.create(mapCache.get(1))
                .verifyComplete();
        StepVerifier.create(mapCache.get(3))
                .expectNextCount(1)
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

    static class LogSubscriber implements Subscriber<Student> {

        @Override
        public void onSubscribe(Subscription s) {
            s.request(Long.MAX_VALUE);
        }

        @Override
        public void onNext(Student student) {
            log.info("St: {}", student);
        }

        @Override
        public void onError(Throwable t) {
            log.error("Error", t);
        }

        @Override
        public void onComplete() {
            log.info("Completed");
        }
    }
}
