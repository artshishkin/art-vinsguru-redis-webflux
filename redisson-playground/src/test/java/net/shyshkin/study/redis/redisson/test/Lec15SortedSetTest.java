package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;
import java.util.function.Function;

@Slf4j
public class Lec15SortedSetTest extends BaseTest {

    @Test
    void sortedSetTest() {
        //given
        RScoredSortedSetReactive<String> sortedSet = this.client.getScoredSortedSet("student:score", StringCodec.INSTANCE);

        //when
        String art = "art" + UUID.randomUUID();
        String kate = "kate" + UUID.randomUUID();
        String arina = "arina" + UUID.randomUUID();

        Mono<Void> testPipeline = Mono
                .from(sortedSet.addScore(art, 12.25))
                .then(sortedSet.add(24.35, kate))
                .then(sortedSet.addScore(kate, 1.11))
                .then(sortedSet.add(6.0, arina))
                .then(sortedSet.add(3.0, arina))
                .then();

        //then
        StepVerifier
                .create(testPipeline)
                .verifyComplete();

        StepVerifier.create(sortedSet.getScore(art))
                .expectNext(12.25)
                .verifyComplete();
        StepVerifier.create(sortedSet.getScore(kate))
                .expectNext(25.46)
                .verifyComplete();
        StepVerifier.create(sortedSet.getScore(arina))
                .expectNext(3.0)
                .verifyComplete();

        Mono<Void> collectionScored = sortedSet.entryRange(0, 1)
                .flatMapIterable(Function.identity())
                .doOnNext(scoredEntry -> log.info("{}: {}", scoredEntry.getValue(), scoredEntry.getScore()))
                .then();

        StepVerifier.create(collectionScored)
                .verifyComplete();

    }
}
