package net.shyshkin.study.redis.performance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RedisSpringDataPerformanceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisSpringDataPerformanceApplication.class, args);
    }

}
