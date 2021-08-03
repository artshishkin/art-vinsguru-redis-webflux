package net.shyshkin.study.redis.redisspring.geo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.geo.dto.GeoLocation;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.geo.GeoSearchArgs;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.function.Function;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantLocatorService {

    private final RGeoReactive<Restaurant> geo;
    private final RMapReactive<String, GeoLocation> map;

    public Flux<Restaurant> getRestaurants(final String zipcode) {
        return getRestaurants(zipcode, 10);
    }

    public Flux<Restaurant> getRestaurants(final String zipcode, final int radius) {
        return map
                .get(zipcode)
                .map(gl -> GeoSearchArgs
                        .from(gl.getLongitude(), gl.getLatitude())
                        .radius(radius, GeoUnit.KILOMETERS)
                )
                .flatMap(geo::search)
                .flatMapIterable(Function.identity())
                .doOnNext(restaurant -> log.debug("search restaurants: {}", restaurant));
    }
}
