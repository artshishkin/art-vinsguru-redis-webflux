package net.shyshkin.study.redis.redisspring.geo.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.geo.dto.GeoLocation;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.ReactiveGeoOperations;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Slf4j
@Service
@RequiredArgsConstructor
public class RestaurantLocatorService {

    private final ReactiveRedisTemplate<String, Restaurant> restaurantReactiveRedisTemplate;
    private final ReactiveRedisTemplate<String, GeoLocation> geoLocationReactiveRedisTemplate;

    public Flux<Restaurant> getRestaurants(final String zipcode) {
        return getRestaurants(zipcode, 10);
    }

    public Flux<Restaurant> getRestaurants(final String zipcode, final int radius) {

        ReactiveGeoOperations<String, Restaurant> geoOperations = restaurantReactiveRedisTemplate.opsForGeo();
        ReactiveHashOperations<String, String, GeoLocation> hashOperations = geoLocationReactiveRedisTemplate.opsForHash();

        return hashOperations
                .get("us:texas", zipcode)
                .map(gl -> new Circle(new Point(gl.getLongitude(), gl.getLatitude()), new Distance(radius, Metrics.KILOMETERS)))
                .flatMapMany(circle -> geoOperations.radius("restaurants", circle))
                .map(res -> res.getContent().getName())
                .doOnNext(restaurant -> log.debug("search restaurants: {}", restaurant));
    }
}
