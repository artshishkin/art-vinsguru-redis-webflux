package net.shyshkin.study.redis.redisspring.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopicReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.StringCodec;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatRoomService implements WebSocketHandler {

    private final RedissonReactiveClient redissonClient;

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {

        String room = getChatRoomName(webSocketSession);

        RTopicReactive topic = this.redissonClient.getTopic(room, StringCodec.INSTANCE);

        //subscribe
        Mono<Void> subscribe = webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(topic::publish)
                .doOnError(thr -> log.error("Error", thr))
                .doFinally(st -> log.debug("Subscriber finally: {}", st))
                .then();

        //publisher
        Flux<WebSocketMessage> publisher = topic.getMessages(String.class)
                .map(webSocketSession::textMessage)
                .doOnError(thr -> log.error("Error", thr))
                .doFinally(st -> log.debug("Publisher finally: {}", st));

        Mono<Void> send = webSocketSession.send(publisher);

        return subscribe.then(send);
    }

    private String getChatRoomName(WebSocketSession socketSession) {
        URI uri = socketSession.getHandshakeInfo().getUri();
        UriComponents uriComponents = UriComponentsBuilder.fromUri(uri).build();
        return uriComponents
                .getQueryParams()
                .toSingleValueMap()
                .getOrDefault("room", "all-together");
    }
}
