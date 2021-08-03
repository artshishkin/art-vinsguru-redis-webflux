package net.shyshkin.study.redis.redisspring.chat.publisher;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ReactiveRedisMessagePublisher {

    private final ReactiveStringRedisTemplate redisTemplate;

    public Mono<Long> publish(String topic, String message) {
        return redisTemplate.convertAndSend(topic, message);
    }
}
