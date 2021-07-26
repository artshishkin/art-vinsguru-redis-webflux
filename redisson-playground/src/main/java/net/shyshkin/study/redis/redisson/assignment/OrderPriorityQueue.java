package net.shyshkin.study.redis.redisson.assignment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OrderPriorityQueue {

    private final RScoredSortedSetReactive<Order> sortedSet;

    public OrderPriorityQueue(RedissonReactiveClient client) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.sortedSet = client.getScoredSortedSet(
                "order-queue",
                new TypedJsonJacksonCodec(Order.class, objectMapper)
        );
    }

    public Mono<Boolean> add(Order order) {
        return sortedSet.add(getScore(order), order);
    }

    public Mono<Integer> addAll(Collection<Order> orders) {

        Map<Order, Double> ordersMap = orders.stream()
                .collect(Collectors.toMap(Function.identity(), this::getScore));

        return sortedSet.addAll(ordersMap);
    }

    public Mono<Order> pollFirst() {
        return sortedSet.pollFirst();
    }

    public Flux<Order> pollFirst(int count) {
        return sortedSet
                .pollFirst(count)
                .flatMapIterable(Function.identity());
    }

    public Flux<Order> takeItems() {
        return sortedSet
                .takeFirstElements()
                .limitRate(1);
    }

    public Mono<Integer> size() {
        return sortedSet.size();
    }

    private double getScore(Order order) {
        return order.getRank().ordinal() + Double.parseDouble("0." + System.nanoTime());
    }
}
