package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.config.RedissonConfig;
import net.shyshkin.study.redis.redisson.test.dto.Student;
import org.junit.jupiter.api.BeforeAll;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;

@Slf4j
public class Lec08LocalCachedMapTest extends BaseTest {

    private RedissonClient redissonClient;
    private RLocalCachedMap<Integer, Student> studentsMap;

    @BeforeAll
    void beforeAll() {
        redissonClient = new RedissonConfig().getClient();

        LocalCachedMapOptions<Integer, Student> mapOptions = LocalCachedMapOptions.<Integer, Student>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.NONE);

        studentsMap = redissonClient.getLocalCachedMap(
                "students",
                new TypedJsonJacksonCodec(Integer.class, Student.class),
                mapOptions
        );
    }
}
