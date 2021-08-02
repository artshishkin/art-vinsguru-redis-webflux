package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.dto.GeoLocation;
import net.shyshkin.study.redis.redisson.test.dto.Restaurant;
import net.shyshkin.study.redis.redisson.test.util.RestaurantUtil;
import org.junit.jupiter.api.*;
import org.redisson.api.GeoUnit;
import org.redisson.api.RGeoReactive;
import org.redisson.api.RMapReactive;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.api.geo.OptionalGeoSearch;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Lec17GeoSpatialTest extends BaseTest {

    private RGeoReactive<Restaurant> geo;
    private RMapReactive<String, GeoLocation> map;

    @BeforeAll
    void setGeo() {
        this.geo = client.getGeo("restaurants", new TypedJsonJacksonCodec(Restaurant.class));
        this.map = client.getMap("us:texas", new TypedJsonJacksonCodec(String.class, GeoLocation.class));
    }

    @Test
    @Order(10)
    void add() {

        //when
        Mono<Void> mono = Flux.fromIterable(RestaurantUtil.getRestaurants())
                .flatMap(r -> geo.add(r.getLongitude(), r.getLatitude(), r).thenReturn(r))
                .flatMap(r -> map.fastPut(r.getZip(), GeoLocation.of(r.getLongitude(), r.getLatitude())))
                .then();

        //then
        StepVerifier.create(mono)
                .verifyComplete();

    }

    @Test
    @Order(20)
    void searchByCoordinates() {
        //given
        //"longitude": -102.891455,
        //"latitude": 31.580721,

        double searchLongitude = -102.891455;
        double searchLatitude = 31.580721;

        OptionalGeoSearch radius = GeoSearchArgs
                .from(searchLongitude, searchLatitude)
                .radius(10, GeoUnit.KILOMETERS);

        //when
        Flux<Restaurant> restaurantFlux = geo.search(radius)
                .flatMapIterable(Function.identity())
                .doOnNext(restaurant -> log.info("search restaurants: {}", restaurant));

        //then
        StepVerifier.create(restaurantFlux.count())
                .assertNext(count -> assertThat(count).isGreaterThanOrEqualTo(1))
                .verifyComplete();
    }

    @Test
    @Order(30)
    void searchByZipcode() {
        //given
        String zipCode = "79756";

        //when
        Flux<Restaurant> restaurantFlux = map
                .get(zipCode)
                .map(gl -> GeoSearchArgs
                        .from(gl.getLongitude(), gl.getLatitude())
                        .radius(10, GeoUnit.KILOMETERS)
                )
                .flatMap(radius -> geo.search(radius))
                .flatMapIterable(Function.identity())
                .doOnNext(restaurant -> log.info("search restaurants: {}", restaurant));

        //then
        StepVerifier.create(restaurantFlux.count())
                .assertNext(count -> assertThat(count).isGreaterThanOrEqualTo(1))
                .verifyComplete();
    }

}
