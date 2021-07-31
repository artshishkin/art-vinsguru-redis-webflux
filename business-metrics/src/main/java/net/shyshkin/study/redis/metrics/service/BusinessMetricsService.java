package net.shyshkin.study.redis.metrics.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.metrics.dto.Product;
import org.redisson.api.RMapReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@Service
public class BusinessMetricsService {

    private final RedissonReactiveClient redissonClient;
    private final RMapReactive<Integer, Product> productsMap;

    public BusinessMetricsService(RedissonReactiveClient redissonClient) {
        this.redissonClient = redissonClient;
        this.productsMap = redissonClient.getMap("products", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }

    public Mono<Map<Product, Double>> getTopProducts(int limit) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        RScoredSortedSetReactive<Integer> topProductsSet = redissonClient.getScoredSortedSet("products:visit:" + date, IntegerCodec.INSTANCE);
        return topProductsSet
                .entryRangeReversed(0, limit - 1)
                .flatMapIterable(Function.identity())
                .flatMapSequential(se -> productsMap
                        .get(se.getValue())
                        .switchIfEmpty(Mono.fromSupplier(() -> Product.builder().id(se.getValue()).description("NOT PRESENT IN PRODUCTS CACHE").build()))
                        .map(pr -> new ScoredEntry<>(se.getScore(), pr)))
                .collectMap(ScoredEntry::getValue, ScoredEntry::getScore, LinkedHashMap::new);
    }

}
