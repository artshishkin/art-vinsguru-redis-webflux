package net.shyshkin.study.redis.redisson.test.config;

import org.redisson.Redisson;
import org.redisson.RedissonReactive;
import org.redisson.api.RedissonClient;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.RedisClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;

import java.util.Objects;

public class RedissonConfig {

    private final String serverUri = "redis://127.0.0.1:6379";

    private RedissonClient redissonClient = null;

    public RedissonClient getClient() {

        if (Objects.isNull(redissonClient)) {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress(serverUri);
            redissonClient = Redisson.create(config);
        }
        return redissonClient;
    }

    public RedissonReactiveClient getReactiveClient() {
        return getClient().reactive();
    }
}
