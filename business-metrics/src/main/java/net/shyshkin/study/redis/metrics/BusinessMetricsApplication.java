package net.shyshkin.study.redis.metrics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BusinessMetricsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessMetricsApplication.class, args);
    }

}
