package net.shyshkin.study.redis.redisspring.fib.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

//    @Bean
//    public CacheManager cacheManager(RedissonClient client) {
//        return new RedissonSpringCacheManager(client);
//    }
}
