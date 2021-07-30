package net.shyshkin.study.redis.performance.service;

import org.redisson.api.BatchOptions;
import org.redisson.api.RBatchReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Autowired;
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
public class VinsProductVisitService {

    @Autowired
    private RedissonReactiveClient client;

    private Sinks.Many<Integer> idSink;

    public VinsProductVisitService() {
        this.idSink = Sinks.many().unicast().onBackpressureBuffer();
    }

    @PostConstruct
    private void init() {
        idSink
                .asFlux()
                .buffer(Duration.ofSeconds(3))
                .flatMap(this::updateBatch)
                .subscribe();
    }

    private Mono<Void> updateBatch(List<Integer> idList) {
        RBatchReactive batch = client.createBatch(BatchOptions.defaults());
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        RScoredSortedSetReactive<Integer> topProductsSet = batch.getScoredSortedSet("products:visit:" + date, IntegerCodec.INSTANCE);

        Map<Integer, Integer> idCount = idList.stream()
                .collect(Collectors.toMap(Function.identity(), i -> 1, Integer::sum));

        Flux
                .fromIterable(idCount.entrySet())
                .doOnNext(entry -> topProductsSet.addScore(entry.getKey(), entry.getValue()))
                .subscribe();

        return batch.execute().then();
    }

}
