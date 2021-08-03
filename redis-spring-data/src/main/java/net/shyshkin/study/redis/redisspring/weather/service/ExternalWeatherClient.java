package net.shyshkin.study.redis.redisspring.weather.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Slf4j
@Service
public class ExternalWeatherClient {

    @CachePut(value = "weather", key = "#zipcode")
    public int getWeatherInfo(int zipcode) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int temperature = ThreadLocalRandom.current().nextInt(10, 40);
        log.debug("Getting new temperature for {}: {}Celsius", zipcode, temperature);
        return temperature;
    }
}
