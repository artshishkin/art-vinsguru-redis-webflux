package net.shyshkin.study.redis.redisspring.chat.subscriber;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

//@Service
public class RedisMessageSubscriber implements MessageListener {
    @Override
    public void onMessage(Message message, byte[] pattern) {

    }
}
