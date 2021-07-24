package net.shyshkin.study.redis.redisson.test;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RPatternTopicReactive;
import org.redisson.api.RTopicReactive;
import org.redisson.api.listener.PatternMessageListener;
import org.redisson.client.codec.StringCodec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
public class Lec12_2_PubSubPatternTest extends BaseTest {

    @Test
    void subscriber1() {
        //given
        RTopicReactive topic = this.client.getTopic("slack-room1", StringCodec.INSTANCE);

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
        RPatternTopicReactive patternTopic = this.client.getPatternTopic("slack-room*", StringCodec.INSTANCE);

        //when
        Mono<Integer> addListener = patternTopic
                .addListener(String.class, new PatternMessageListener<String>() {
                    @Override
                    public void onMessage(CharSequence pattern, CharSequence channel, String msg) {
                        log.info("Pattern: {}, Channel: {}, Message: {}", pattern, channel, msg);
                    }
                })
                .doOnNext(listenerId -> log.info("Local JVM unique listener ID: {}", listenerId));

        //then
        StepVerifier.create(addListener)
                .expectNextCount(1)
                .verifyComplete();

        Thread.sleep(600_000);
    }

    @Test
    void publisher1() {

        //given
        RTopicReactive topic = this.client.getTopic("slack-room1", StringCodec.INSTANCE);

        //when
        Mono<Long> publish = topic
                .publish(FAKER.lorem().sentence())
                .doOnNext(subscribersCount -> log.info("Subscribers count: {}", subscribersCount));

        //then
        StepVerifier.create(publish)
                .expectNext(2L)
                .verifyComplete();
    }

    @Test
    void publisher2() {

        //given
        RTopicReactive topic = this.client.getTopic("slack-room2", StringCodec.INSTANCE);

        //when
        Mono<Long> publish = topic
                .publish(FAKER.lorem().sentence())
                .doOnNext(subscribersCount -> log.info("Subscribers count: {}", subscribersCount));

        //then
        StepVerifier.create(publish)
                .expectNext(1L)
                .verifyComplete();
    }
}
