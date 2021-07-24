package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec13BatchTest extends BaseTest {

    // 500_000 ~7s100ms
    @Test
    void batchTest() {

        //given
        RBatchReactive batch = this.client.createBatch(BatchOptions.defaults());

        //when
        RListReactive<Long> batchList = batch.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> batchSet = batch.getSet("numbers-set", LongCodec.INSTANCE);

        // 20_000 ~1s500ms
        // 500_000 ~7s100ms
        for (long i = 0; i < 20_000; i++) {
            batchList.add(i);   // despite `add` method returns Mono we do not have to subscribe manually
            batchSet.add(i);    // it will be subscribed when we call batch.execute
        }

        Mono<BatchResult<?>> execute = batch
                .execute();

        //then
        StepVerifier
                .create(execute)
                .expectNextCount(1)
                .verifyComplete();
    }
    // 20_000 ~4s 100ms
    // 500_000 ~1 m 1 s 900ms
    @Test
    void regularTest_oneByOne() {

        //given
        RListReactive<Long> list = client.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = client.getSet("numbers-set", LongCodec.INSTANCE);

        //when
        Mono<Void> oneByOne = Flux
                .range(1, 20_000)
                .map(Long::valueOf)
                .flatMap(item -> list.add(item).then(set.add(item)))
                .then();

        //then
        StepVerifier
                .create(oneByOne)
                .verifyComplete();
    }

    // 20_000 ~ 24s 500ms
    // 500_000 ~9 m 44 s 300ms
    @Test
    void regularTest_addAll() {

        //given
        RListReactive<Long> list = client.getList("numbers-list", LongCodec.INSTANCE);
        RSetReactive<Long> set = client.getSet("numbers-set", LongCodec.INSTANCE);

        //when
        Flux<Long> addAll = Flux
                .range(1, 20_000)
                .map(Long::valueOf);
        Mono<Void> listAddAll = list.addAll(addAll).then();
        Mono<Void> setAddAll = set.addAll(addAll).then();

        //then
        StepVerifier
                .create(listAddAll.concatWith(setAddAll))
                .verifyComplete();
    }
}
