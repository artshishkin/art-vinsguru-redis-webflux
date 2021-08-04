package net.shyshkin.study.redis.redisspring.geo.config;

import net.shyshkin.study.redis.redisspring.geo.dto.GeoLocation;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class GeoDataRedisConfig {

    @Bean
    public ReactiveRedisTemplate<String, Restaurant> restaurantReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {

        return new ReactiveRedisTemplate<String, Restaurant>(connectionFactory,
                RedisSerializationContext.<String, Restaurant>newSerializationContext(RedisSerializer.string())
                        .value(new Jackson2JsonRedisSerializer<>(Restaurant.class))
                        .build());
    }

    @Bean
    public ReactiveRedisTemplate<String, GeoLocation> geoLocationReactiveRedisTemplate(ReactiveRedisConnectionFactory connectionFactory) {
        return new ReactiveRedisTemplate<String, GeoLocation>(connectionFactory,
                RedisSerializationContext.<String, GeoLocation>newSerializationContext(RedisSerializer.string())
                        .hashValue(new Jackson2JsonRedisSerializer<>(GeoLocation.class))
                        .build());
    }


}
