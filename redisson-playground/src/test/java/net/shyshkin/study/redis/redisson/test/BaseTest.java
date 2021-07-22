package net.shyshkin.study.redis.redisson.test;

import com.github.javafaker.Faker;
import net.shyshkin.study.redis.redisson.test.config.RedissonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RedissonReactiveClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    protected RedissonReactiveClient client;
    private final RedissonConfig redissonConfig = new RedissonConfig();
    protected static final Faker FAKER = Faker.instance();

    @BeforeAll
    void setClient() {
        client = redissonConfig.getReactiveClient();
    }

    @AfterAll
    void shutdown() {
        client.shutdown();
    }
}
