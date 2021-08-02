package net.shyshkin.study.redis.redisspring.geo.config;

import net.shyshkin.study.redis.redisspring.geo.dto.GeoLocation;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoDataRedisConfig {

    @Bean
    public RGeoReactive<Restaurant> geo(RedissonReactiveClient client) {
        return client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
    }

    @Bean
    public RMapReactive<String, GeoLocation> map(RedissonReactiveClient client){
        return  client.getMap("us:texas", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }
}
