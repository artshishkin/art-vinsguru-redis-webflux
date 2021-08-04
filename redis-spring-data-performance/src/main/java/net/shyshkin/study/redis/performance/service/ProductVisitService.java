package net.shyshkin.study.redis.performance.service;

import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProductVisitService {

    private final ReactiveRedisTemplate<String, Integer> visitReactiveRedisTemplate;

    private final Sinks.Many<Integer> idSink;

    public ProductVisitService(ReactiveRedisTemplate<String, Integer> visitReactiveRedisTemplate) {
        this.visitReactiveRedisTemplate = visitReactiveRedisTemplate;
        this.idSink = Sinks.many().unicast().onBackpressureBuffer();
    }

    @PostConstruct
    private void init() {
        idSink
                .asFlux()
                .buffer(Duration.ofSeconds(3))
                .flatMap(this::updateNoBatch)
                .subscribe();
    }

    public void addVisit(int visitId) {
        idSink.tryEmitNext(visitId);
    }

    private Mono<Void> updateNoBatch(List<Integer> idList) {

        ReactiveZSetOperations<String, Integer> topProductsSet = visitReactiveRedisTemplate.opsForZSet();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        Map<Integer, Long> idCount = idList.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        return Flux
                .fromIterable(idCount.entrySet())
                .flatMap(entry -> topProductsSet.incrementScore("products:visit:" + date, entry.getKey(), entry.getValue()))
                .then();
    }
}
