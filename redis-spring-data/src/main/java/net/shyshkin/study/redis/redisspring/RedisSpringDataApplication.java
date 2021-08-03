package net.shyshkin.study.redis.redisspring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RedisSpringDataApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedisSpringDataApplication.class, args);
    }

}
