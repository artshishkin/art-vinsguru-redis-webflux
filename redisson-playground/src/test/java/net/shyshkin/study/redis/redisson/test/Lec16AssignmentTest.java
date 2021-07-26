package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.assignment.Order;
import net.shyshkin.study.redis.redisson.assignment.OrderPriorityQueue;
import net.shyshkin.study.redis.redisson.assignment.UserRank;
import net.shyshkin.study.redis.redisson.test.subscriber.LogSubscriber;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static net.shyshkin.study.redis.redisson.assignment.UserRank.*;

@Slf4j
public class Lec16AssignmentTest extends BaseTest {

    private OrderPriorityQueue priorityQueue;

    @BeforeAll
    void initQueue() {
        priorityQueue = new OrderPriorityQueue(this.client);
    }

    @Test
    void assignment() {
        //given
        List<Order> orders = List.of(
                order(PRIME, "Tesla", 1),
                order(GUEST, "Milk", 20),
                order(STD, "iPhone", 4),
                order(PRIME, "Motorola", 1),
                order(GUEST, "Cookie", 100)
        );
        int size = orders.size();

        //when
        Mono<Integer> insertion = priorityQueue.addAll(orders);

        //then
        StepVerifier.create(insertion.then())
                .verifyComplete();

        StepVerifier.create(priorityQueue.size())
                .expectNext(size)
                .verifyComplete();

        Flux<UserRank> rankFlux = priorityQueue
                .pollFirst(size)
                .doOnNext(order -> log.info("{}", order))
                .map(Order::getRank);

        StepVerifier.create(rankFlux)
                .expectNext(PRIME, PRIME, STD, GUEST, GUEST)
                .verifyComplete();

        StepVerifier.create(priorityQueue.size())
                .expectNext(0)
                .verifyComplete();
    }

    @Test
    void producerSimple() {
        //given
        List<Order> orders = List.of(
                orderRandom(PRIME),
                orderRandom(GUEST),
                orderRandom(STD),
                orderRandom(PRIME),
                orderRandom(GUEST)
        );

        //when
        Mono<Integer> insertion = priorityQueue.addAll(orders);

        //then
        StepVerifier.create(insertion.then())
                .verifyComplete();
    }

    @Test
    void producerEternity() {

        //given
        Flux<Integer> insertion = Flux.interval(Duration.ofSeconds(1))
                .flatMap(interval -> addRandomOrders());

        //then
        StepVerifier.create(insertion.then())
                .verifyComplete();
    }

    private Mono<Integer> addRandomOrders() {
        List<Order> orders = List.of(
                orderRandom(PRIME),
                orderRandom(GUEST),
                orderRandom(STD),
                orderRandom(PRIME),
                orderRandom(GUEST)
        );

        return priorityQueue
                .addAll(orders)
                .doOnNext(count -> log.info("Added {} new orders", count));
    }

    @Test
    void consumer() throws InterruptedException {

        //when
        priorityQueue.takeItems()
                .delayElements(Duration.ofMillis(300))
                .subscribe(new LogSubscriber("Cons1"));

        //then
        Thread.sleep(600_000);
    }

    private Order order(UserRank rank, String product, int amount) {
        return Order.builder()
                .amount(amount)
                .product(product)
                .rank(rank)
                .id(UUID.randomUUID())
                .date(LocalDateTime.now())
                .build();
    }

    private Order orderRandom(UserRank rank) {
        return order(rank, FAKER.commerce().productName(), FAKER.random().nextInt(1, 100));
    }
}
