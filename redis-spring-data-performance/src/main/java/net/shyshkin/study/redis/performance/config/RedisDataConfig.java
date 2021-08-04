package net.shyshkin.study.redis.performance.config;

import net.shyshkin.study.redis.performance.entity.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisDataConfig {

    @Bean
    public ReactiveRedisTemplate<String, Product> productReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory,
                RedisSerializationContext.<String, Product>newSerializationContext(RedisSerializer.string())
                        .hashValue(new Jackson2JsonRedisSerializer<>(Product.class))
                        .hashKey(new Jackson2JsonRedisSerializer<>(Integer.class))
                        .build());
    }

    @Bean
    public ReactiveRedisTemplate<String, Integer> visitReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<>(connectionFactory,
                RedisSerializationContext.<String, Integer>newSerializationContext(RedisSerializer.string())
                        .value(new Jackson2JsonRedisSerializer<>(Integer.class))
                        .build());
    }
}
