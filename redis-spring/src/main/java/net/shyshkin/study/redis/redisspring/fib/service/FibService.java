package net.shyshkin.study.redis.redisspring.fib.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FibService {

//    @Cacheable(value = "math:fib", key = "#p0")
    @Cacheable(value = "math:fib", key = "#index")
    public long getFib(int index, String name) {
        log.debug("Calculating fib for {} and user {}", index, name);
        return fib(index);
    }

    //intentional 2^N - worst algorithm - for study
    private long fib(int index) {
        if (index < 0) throw new IllegalArgumentException("Argument must be non-negative");
        return (index < 2) ?
                index :
                fib(index - 1) + fib(index - 2);
    }
}
