package net.shyshkin.study.redis.performance.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.performance.service.BusinessMetricsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Map;

@RestController("vinsBusinessMetricsController")
@RequestMapping("business/metrics")
@RequiredArgsConstructor
public class BusinessMetricsController {

    private final BusinessMetricsService businessMetricsService;

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Map<Integer, Double>> getProductUpdates() {
        return businessMetricsService
                .getTopProducts(3)
                .repeatWhen(l->Flux.interval(Duration.ofSeconds(3))); //create companion Flux
    }
}
