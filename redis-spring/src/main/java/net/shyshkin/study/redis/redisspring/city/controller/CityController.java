package net.shyshkin.study.redis.redisspring.city.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.city.dto.City;
import net.shyshkin.study.redis.redisspring.city.service.CityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("city")
@RequiredArgsConstructor
public class CityController {

    private final CityService cityService;

    @GetMapping("{zipcode}")
    public Mono<City> getCityInfo(@PathVariable String zipcode) {
        return cityService
                .getCityInfo(zipcode)
                .doOnNext(city -> log.debug("Retrieved: {}", city));
    }
}
