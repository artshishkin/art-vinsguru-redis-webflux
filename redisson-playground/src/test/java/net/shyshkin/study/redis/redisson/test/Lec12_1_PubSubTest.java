package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisson.test.subscriber.LogSubscriber;
import org.junit.jupiter.api.Test;
import org.redisson.api.RTopicReactive;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec12_1_PubSubTest extends BaseTest {

    @Test
    void subscriber1() {
        //given
        RTopicReactive topic = this.client.getTopic("slack-room", StringCodec.INSTANCE);

        //when
        Flux<String> messages = topic.getMessages(String.class);

        //then
        StepVerifier.create(messages)
                .thenConsumeWhile(mess -> true, mess -> log.info("{}", mess))
                .verifyComplete();
    }

    @Test
    void subscriber2() throws InterruptedException {
        //given
        RTopicReactive topic = this.client.getTopic("slack-room", StringCodec.INSTANCE);

        //when
        Flux<String> messages = topic.getMessages(String.class);

        //then
        messages.subscribe(new LogSubscriber("Subscriber 2"));

        Thread.sleep(60_000);
    }

    @Test
    void publisher() {

        //given
        RTopicReactive topic = this.client.getTopic("slack-room", StringCodec.INSTANCE);

        //when
        Mono<Long> publish = topic
                .publish(FAKER.lorem().sentence())
                .doOnNext(subscribersCount -> log.info("Subscribers count: {}", subscribersCount));

        //then
        StepVerifier.create(publish)
                .expectNextCount(1)
                .verifyComplete();
    }
}
