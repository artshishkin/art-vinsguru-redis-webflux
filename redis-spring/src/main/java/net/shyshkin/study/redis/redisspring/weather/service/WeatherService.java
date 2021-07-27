package net.shyshkin.study.redis.redisspring.weather.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class WeatherService {

    private final ExternalWeatherClient externalWeatherClient;

    @Cacheable("weather")
    public int getInfo(int zipcode) {
//        return externalWeatherClient.getWeatherInfo(zipcode); //NO NEED TO do this - we take info from cache
        return 0;
    }

    @Scheduled(fixedRateString = "${app.weather.update.period:10_000}")
    public void update() {
        IntStream
                .rangeClosed(1, 5)
                .parallel()
                .forEach(externalWeatherClient::getWeatherInfo);
    }
}
