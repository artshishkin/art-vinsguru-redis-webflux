package net.shyshkin.study.redis.redisson.test;

import net.shyshkin.study.redis.redisson.test.config.RedissonConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.redisson.api.RedissonReactiveClient;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BaseTest {

    protected RedissonReactiveClient client;
    private final RedissonConfig redissonConfig = new RedissonConfig();

    @BeforeAll
    void setClient() {
        client = redissonConfig.getReactiveClient();
    }

    @AfterAll
    void shutdown() {
        client.shutdown();
    }
}
