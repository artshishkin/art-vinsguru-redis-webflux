package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.dto.Restaurant;
import net.shyshkin.study.redis.redisson.test.util.RestaurantUtil;
import org.junit.jupiter.api.Test;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class Lec17GeoSpatialTest extends BaseTest {

    @Test
    void add() {
        //given
        RGeoReactive<Restaurant> geo = client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));

        //when
        Mono<Void> mono = Flux.fromIterable(RestaurantUtil.getRestaurants())
                .flatMap(r -> geo.add(r.getLongitude(), r.getLatitude(), r))
                .then();

        //then
        StepVerifier.create(mono)
                .verifyComplete();

        //"longitude": -102.891455,
        //"latitude": 31.580721,

        double searchLongitude = -102.891455;
        double searchLatitude = 31.580721;

        OptionalGeoSearch radius = GeoSearchArgs
                .from(searchLongitude, searchLatitude)
                .radius(10, GeoUnit.KILOMETERS);

        Flux<Restaurant> restaurantFlux = geo.search(radius)
                .flatMapIterable(Function.identity())
                .doOnNext(restaurant -> log.info("search restaurants: {}", restaurant));

        StepVerifier.create(restaurantFlux.count())
                .assertNext(count -> assertThat(count).isGreaterThanOrEqualTo(1))
                .verifyComplete();
    }
}
