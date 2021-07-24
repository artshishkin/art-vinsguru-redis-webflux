package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RBucketReactive;
import org.redisson.api.RTransactionReactive;
import org.redisson.api.TransactionOptions;
import org.redisson.client.codec.LongCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec14TransactionTest extends BaseTest {

    private RBucketReactive<Long> user1Balance;
    private RBucketReactive<Long> user2Balance;

    @BeforeEach
    void accountSetup() {

        user1Balance = this.client.getBucket("user:1:balance", LongCodec.INSTANCE);
        user2Balance = this.client.getBucket("user:2:balance", LongCodec.INSTANCE);

        Mono<Void> setupAccounts = Mono
                .from(user1Balance.set(100L))
                .then(user2Balance.set(0L))
                .then();

        StepVerifier.create(setupAccounts).verifyComplete();
    }

    @AfterEach
    void accountBalancesStatus() {
        Mono<Void> logBalances = Mono
                .zip(this.user1Balance.get(), this.user2Balance.get())
                .doOnNext(t -> log.info("user:1:balance = {}, user:2:balance = {}", t.getT1(), t.getT2()))
                .then();

        StepVerifier.create(logBalances).verifyComplete();
    }

    @Test
    void nonTransactionTest_my() throws InterruptedException {

        //when
        transfer(user1Balance, user2Balance, 40)
                .then(Mono.error(new RuntimeException("Some error happened in the pipeline")))
                .subscribe();

        //then
        Thread.sleep(1000);

    }

    @Test
    void nonTransactionTest_vinsguru() throws InterruptedException {

        //when
        transferVinsguru(user1Balance, user2Balance, 40)
                .thenReturn(0)
                .map(i -> 5 / i) //emulate some error
                .doOnError(error -> log.error("Error", error))
                .subscribe();

        //then
        Thread.sleep(1000);

    }

    @Test
    void transactionTest_my() throws InterruptedException {

        //given
        RTransactionReactive transaction = client.createTransaction(TransactionOptions.defaults());

        //when
        RBucketReactive<Long> user1Balance = transaction.getBucket("user:1:balance", LongCodec.INSTANCE);
        RBucketReactive<Long> user2Balance = transaction.getBucket("user:2:balance", LongCodec.INSTANCE);

        transfer(user1Balance, user2Balance, 40)
                .then(Mono.error(new RuntimeException("Some error happened in the pipeline")))
                .then(transaction.commit())
                .doOnError(error -> log.info("Error: {}", error.toString()))
                .doOnError(error -> transaction.rollback())
                .subscribe();

        //then
        Thread.sleep(1000);

    }

    private Mono<Void> transfer(RBucketReactive<Long> fromAccount, RBucketReactive<Long> toAccount, long amount) {

        return Mono.zip(fromAccount.get(), toAccount.get())
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> fromAccount.set(t.getT1() - amount).then(toAccount.set(t.getT2() + amount)))
                .then();
    }

    private Mono<Void> transferVinsguru(RBucketReactive<Long> fromAccount, RBucketReactive<Long> toAccount, long amount) {

        return Flux.zip(fromAccount.get(), toAccount.get())
                .filter(t -> t.getT1() >= amount)
                .flatMap(t -> fromAccount.set(t.getT1() - amount).thenReturn(t))
                .flatMap(t -> toAccount.set(t.getT2() + amount).thenReturn(t))
                .then();
    }
}
