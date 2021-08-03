package net.shyshkin.study.redis.redisspring.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.redisspring.chat.publisher.ReactiveRedisMessagePublisher;
import org.springframework.data.redis.connection.ReactiveSubscription;
import org.springframework.data.redis.core.ReactiveListOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
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

    private final ReactiveStringRedisTemplate stringRedisTemplate;
    private final ReactiveRedisMessageListenerContainer chatRoomRedisListenerContainer;
    private final ReactiveRedisMessagePublisher publisher;

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {

        String room = getChatRoomName(webSocketSession);
        ReactiveListOperations<String, String> history = stringRedisTemplate.opsForList();

        //subscribe
        Mono<Void> subscribe = webSocketSession.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(msg -> history
                        .rightPush("history:" + room, msg)
                        .then(publisher.publish(room, msg))
                )
                .doOnError(thr -> log.error("Error", thr))
                .doFinally(st -> log.debug("Subscriber finally: {}", st))
                .then();
        subscribe.subscribe();

        //publisher
        Flux<String> historyMessages = history.range("history:" + room, 0, -1);

        Flux<ReactiveSubscription.Message<String, String>> chatMessagesFlux = chatRoomRedisListenerContainer.receive(ChannelTopic.of(room));
        Flux<String> topicMessages = chatMessagesFlux.map(ReactiveSubscription.Message::getMessage);

        Flux<WebSocketMessage> publisher = topicMessages
                .startWith(historyMessages)
                .map(webSocketSession::textMessage)
                .doOnError(thr -> log.error("Error", thr))
                .doFinally(st -> log.debug("Publisher finally: {}", st));

        Mono<Void> send = webSocketSession.send(publisher);

//        return subscribe.then(send);
        return send;
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
