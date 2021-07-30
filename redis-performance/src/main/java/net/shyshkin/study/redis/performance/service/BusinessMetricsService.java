package net.shyshkin.study.redis.performance.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BusinessMetricsService {

    private final RedissonReactiveClient redissonClient;

    public Mono<Map<Integer, Double>> getTopProducts(int limit) {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        RScoredSortedSetReactive<Integer> topProductsSet = redissonClient.getScoredSortedSet("products:visit:" + date, IntegerCodec.INSTANCE);
        return topProductsSet
                .entryRangeReversed(0, limit - 1)
                .map(listSe -> listSe.stream().collect(
                        Collectors.toMap(
                                ScoredEntry::getValue,
                                ScoredEntry::getScore,
                                (a, b) -> a,
                                LinkedHashMap::new
                        )
                ));
    }

}
