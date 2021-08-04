package net.shyshkin.study.redis.redisspring.geo.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.redisspring.geo.dto.Restaurant;
import net.shyshkin.study.redis.redisspring.geo.service.RestaurantLocatorService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("geo")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantLocatorService locatorService;

    //    @GetMapping(value = "{zipcode}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @GetMapping(value = "{zipcode}")
    public Flux<Restaurant> getRestaurants(@PathVariable String zipcode, @RequestParam(defaultValue = "10") int radius) {
        return locatorService.getRestaurants(zipcode, radius);
    }
}
