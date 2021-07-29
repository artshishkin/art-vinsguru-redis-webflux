package net.shyshkin.study.redis.performance.assignment.config;

import net.shyshkin.study.redis.performance.entity.Product;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

@Configuration
public class SinkConfig {

    @Bean
    public Sinks.Many<List<Product>> sink() {
        return Sinks.many().replay().limit(1);
    }

    @Bean
    public Flux<List<Product>> productBroadcast() {
        return sink().asFlux();
    }

}

