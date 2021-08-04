package net.shyshkin.study.redis.performance.service;

import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataSetupService implements CommandLineRunner {

    private final ProductRepository repository;
    private final R2dbcEntityTemplate entityTemplate;

    private static final Faker FAKER = Faker.instance();

    @Value("${app.sql.init-file}")
    private Resource initSql;

    @Override
    public void run(String... args) throws Exception {
        String query = StreamUtils.copyToString(initSql.getInputStream(), StandardCharsets.UTF_8);
        log.debug("{}", query);

        Mono<Void> createProductTable = entityTemplate
                .getDatabaseClient()
                .sql(query)
                .then();

        Flux<Product> productFlux = Flux
                .range(1, 1000)
                .map(idx -> Product.builder()
                        .description(FAKER.commerce().productName())
                        .price(100.0 * FAKER.random().nextDouble())
                        .build());

        createProductTable
                .thenMany(repository.saveAll(productFlux))
                .subscribe(
                        pr -> {
                            if (pr.getId() % 100 == 0) log.debug("{}", pr);
                        },
                        ex -> log.error("Error", ex),
                        () -> log.debug("Data setup completed successfully")
                );
    }
}
