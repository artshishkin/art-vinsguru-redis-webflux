package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.config.RedissonConfig;
import net.shyshkin.study.redis.redisson.test.dto.Student;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Slf4j
public class Lec08LocalCachedMapTest extends BaseTest {

    private RedissonClient redissonClient;
    private RLocalCachedMap<Integer, Student> studentsMap;

    @BeforeAll
    void beforeAll() {
        redissonClient = new RedissonConfig().getClient();

        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.NONE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE);

        studentsMap = redissonClient.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                mapOptions
        );
    }

    @Test
    void server1() throws InterruptedException {

        //given
        studentsMap.put(1, generateRandomStudent("Art"));
        studentsMap.put(2, generateRandomStudent("Kate"));

        //when
        Flux.interval(Duration.ofSeconds(1))
                .doOnNext(i -> log.info("{}", studentsMap.get(1)))
                .subscribe();

        //then
        log.info("When we start server2 then name of student should change");
        Thread.sleep(600000);
    }

    @Test
    void server2() {

        //when
        Student student = studentsMap.get(1);
        student.setName("Art-updated");
        studentsMap.put(1, student);

        //then
        log.info("Name for server1 should change too");
    }

    @Test
    void server3() {

        //when
        Student student = generateRandomStudent("Art-after-server-down");
        studentsMap.put(1, student);

        //then
        log.info("All data for server1 should change too");
    }

    private Student generateRandomStudent(String name) {
        return Student.builder()
                .name(name)
                .city(FAKER.address().city())
                .age(FAKER.random().nextInt(18, 45))
                .mark(FAKER.random().nextInt(100))
                .mark(FAKER.random().nextInt(100))
                .mark(FAKER.random().nextInt(100))
                .build();
    }
}
