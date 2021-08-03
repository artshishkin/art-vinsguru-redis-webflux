package net.shyshkin.study.redis.redisspring.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;

@Configuration
public class ChatRoomRedisConfig {

//    @Bean
//    MessageListenerAdapter messageListener() {
//        return new MessageListenerAdapter(new RedisMessageSubscriber());
//    }

    @Bean
    ReactiveRedisMessageListenerContainer chatRoomRedisListenerContainer(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        ReactiveRedisMessageListenerContainer container = new ReactiveRedisMessageListenerContainer(reactiveRedisConnectionFactory);
//        container.addMessageListener(messageListener(), topic());
        return container;
    }

}
