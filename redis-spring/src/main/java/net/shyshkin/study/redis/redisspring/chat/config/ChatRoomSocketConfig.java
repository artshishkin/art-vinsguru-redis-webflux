package net.shyshkin.study.redis.redisspring.chat.config;

import net.shyshkin.study.redis.redisspring.chat.service.ChatRoomService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;

import java.util.Map;

@Configuration
public class ChatRoomSocketConfig {

    @Bean
    public HandlerMapping handlerMapping(ChatRoomService chatRoomService) {
        Map<String, ChatRoomService> urlMap = Map.of("/chat", chatRoomService);
        return new SimpleUrlHandlerMapping(urlMap, -1);
    }
}
