package net.shyshkin.study.redis.redisspring.city.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.city.client.CityClient;
import net.shyshkin.study.redis.redisspring.city.dto.City;
import net.shyshkin.study.redis.redisspring.city.repository.CityRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class CityService {

    private final CityRepository cityRepository;
    private final CityClient cityClient;

    @Value("${app.city.info.ttl.value}")
    private long ttlValue;

    @Value("${app.city.info.ttl.unit}")
    private TimeUnit ttlUnit;

    public Mono<City> getCityInfo(String zipcode) {
        return Mono
                .justOrEmpty(cityRepository.findById(zipcode))
                .switchIfEmpty(cityClient.getCityInfo(zipcode).map(cityRepository::save))
                .log()
                .subscribeOn(Schedulers.boundedElastic())
                ;
    }

    @Scheduled(fixedRate = 20_000)
    public void updateCache() {

        log.debug("Update cache started");

        cityClient.getAllCitiesInfo()
                .collectList()
                .doOnNext(list -> log.debug("City list contains {} items", list.size()))
                .map(cityRepository::saveAll)
                .subscribe(
                        list -> log.debug("Cached {} cities", StreamSupport.stream(list.spliterator(), true).count()),
                        ex -> log.error("Error", ex),
                        () -> log.debug("Cache updated successfully")
                );
    }
}
