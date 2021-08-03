package net.shyshkin.study.redis.redisspring.city.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.city.client.CityClient;
import net.shyshkin.study.redis.redisspring.city.dto.City;
import org.redisson.api.RMapCacheReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class CityService {

    private final RMapCacheReactive<String, City> cityMap;
    private final CityClient cityClient;

    @Value("${app.city.info.ttl.value}")
    private long ttlValue;

    @Value("${app.city.info.ttl.unit}")
    private TimeUnit ttlUnit;

    public CityService(RedissonReactiveClient redissonClient, CityClient cityClient) {
        cityMap = redissonClient.getMapCache("city:info", new TypedJsonJacksonCodec(String.class, City.class));
        this.cityClient = cityClient;
    }

    public Mono<City> getCityInfo(String zipcode) {
        return cityMap
                .get(zipcode)
                .switchIfEmpty(cityClient
                        .getCityInfo(zipcode)
                        .flatMap(city -> cityMap
                                .fastPut(city.getZipcode(), city)
                                .thenReturn(city)))
                .onErrorResume(ex -> cityClient.getCityInfo(zipcode));
    }

    @Scheduled(fixedRate = 10_000)
    public void updateCache() {

        log.debug("Started update cache");

        cityClient
                .getAllCitiesInfo()
                .collectMap(City::getZipcode)
                .doOnNext(citiesMap -> log.debug("Trying to update cache of {} cities", citiesMap.size()))
                .flatMap(cityMap::putAll)
                .subscribe(
                        null,
                        ex -> log.error("Error", ex),
                        () -> log.debug("Cache updated successfully")
                );
    }
}
