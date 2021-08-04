package net.shyshkin.study.redis.redisspring.geo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.geo.dto.GeoLocation;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;
import net.shyshkin.study.redis.redisspring.geo.util.RestaurantUtil;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.ReactiveGeoOperations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSetupService implements CommandLineRunner {

    private final ReactiveRedisTemplate<String, Restaurant> restaurantReactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, GeoLocation> geoLocationReactiveRedisTemplate;

    @Override
    public void run(String... args) throws Exception {
        importDataIntoRedis()
                .doFinally(st -> log.debug("Geo data setup finished with result: {}", st))
                .subscribe();
    }

    private Mono<Void> importDataIntoRedis() {

        ReactiveGeoOperations<String, Restaurant> geoOperations = restaurantReactiveRedisTemplate.opsForGeo();
        ReactiveHashOperations<String, String, GeoLocation> hashOperations = geoLocationReactiveRedisTemplate
                .opsForHash();

        return Flux.fromIterable(RestaurantUtil.getRestaurants())
                .flatMap(r -> geoOperations.add("restaurants", new Point(r.getLongitude(), r.getLatitude()), r).thenReturn(r))
                .flatMap(r -> hashOperations.put("us:texas", r.getZip(), GeoLocation.of(r.getLongitude(), r.getLatitude())))
                .then();
    }
}
