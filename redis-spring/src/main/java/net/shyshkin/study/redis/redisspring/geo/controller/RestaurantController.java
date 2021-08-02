package net.shyshkin.study.redis.redisspring.geo.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;
import net.shyshkin.study.redis.redisspring.geo.service.RestaurantLocatorService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("geo")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantLocatorService locatorService;

    @GetMapping(value = "{zipcode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Restaurant> getRestaurants(@PathVariable String zipcode) {
        return locatorService.getRestaurants(zipcode);
    }
}
