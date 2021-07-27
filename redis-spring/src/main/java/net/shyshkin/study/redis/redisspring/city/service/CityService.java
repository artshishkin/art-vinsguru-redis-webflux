package net.shyshkin.study.redis.redisspring.city.service;

import net.shyshkin.study.redis.redisspring.city.client.CityClient;
import net.shyshkin.study.redis.redisspring.city.dto.City;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class CityService {

    private final RMapReactive<String, City> cityMap;
    private final CityClient cityClient;

    public CityService(RedissonReactiveClient redissonClient, CityClient cityClient) {
        cityMap = redissonClient.getMap("city:info", new TypedJsonJacksonCodec(String.class, City.class));
        this.cityClient = cityClient;
    }

    public Mono<City> getCityInfo(String zipcode) {
        return cityMap
                .get(zipcode)
                .switchIfEmpty(askExternalServiceAndCache(zipcode));
    }

    public Mono<City> getCityInfoFast(String zipcode) {
        return cityMap
                .get(zipcode)
                .switchIfEmpty(askExternalServiceAndFastCache(zipcode));
    }

    private Mono<City> askExternalServiceAndCache(String zipcode) {
        return cityClient
                .getCityInfo(zipcode)
                .flatMap(city -> cityMap.put(zipcode, city));
    }

    private Mono<City> askExternalServiceAndFastCache(String zipcode) {
        return cityClient
                .getCityInfo(zipcode)
                .flatMap(city -> cityMap
                        .fastPut(zipcode, city)
                        .thenReturn(city));
    }
}
