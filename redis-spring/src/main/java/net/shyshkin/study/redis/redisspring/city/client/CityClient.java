package net.shyshkin.study.redis.redisspring.city.client;

import net.shyshkin.study.redis.redisspring.city.dto.City;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class CityClient {

    private final WebClient webClient;

    public CityClient(@Value("${app.city.server.uri}") String cityApiServerUri) {
        webClient = WebClient.builder().baseUrl(cityApiServerUri).build();
    }

    public Mono<City> getCityInfo(String zipcode) {

        return webClient
                .get()
                .uri("/{zipcode}", zipcode)
                .retrieve()
                .bodyToMono(City.class);
    }
}
