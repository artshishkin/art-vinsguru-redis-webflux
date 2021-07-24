package net.shyshkin.study.redis.redisson.test.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@Slf4j
@RequiredArgsConstructor
public class LogSubscriber implements Subscriber<Object> {

    private final String name;

    @Override
    public void onSubscribe(Subscription s) {
        s.request(Long.MAX_VALUE);
    }

    @Override
    public void onNext(Object o) {
        log.info("{} - {}", name, o);
    }

    @Override
    public void onError(Throwable t) {
        log.error("Error in {}", name, t);
    }

    @Override
    public void onComplete() {
        log.info("Completed");
    }
}

