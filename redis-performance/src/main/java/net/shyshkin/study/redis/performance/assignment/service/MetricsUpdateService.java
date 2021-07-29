package net.shyshkin.study.redis.performance.assignment.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.performance.entity.Product;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MetricsUpdateService {

    private final Sinks.Many<List<Product>> sink;
    private final ProductVisitService productVisitService;

    @Scheduled(fixedRate = 3_000)
    public void updateSink() {
        productVisitService.getTopProducts(3)
                .doOnNext(sink::tryEmitNext)
                .subscribe();
    }
}
