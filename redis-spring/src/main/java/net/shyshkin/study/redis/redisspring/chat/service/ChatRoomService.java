package net.shyshkin.study.redis.redisspring.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

@Service
public class ChatRoomService implements WebSocketHandler {

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {

        //subscribe
        webSocketSession.receive();

        //publisher
        webSocketSession.send();

        return null;
    }
}
