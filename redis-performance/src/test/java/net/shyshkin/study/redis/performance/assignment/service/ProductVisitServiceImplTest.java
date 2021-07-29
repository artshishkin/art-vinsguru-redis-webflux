package net.shyshkin.study.redis.performance.assignment.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class ProductVisitServiceImplTest {

    private static final int[] VISITS = new int[]{100, 120, 200, 80, 140};
    private static final int TEST_PRODUCT_COUNT = VISITS.length;

    @Autowired
    ProductVisitService productVisitService;

    @Qualifier("cachedProductService")
    @Autowired
    ProductService productService;

    @Test
    void getTopProducts() {

        //given
        List<Product> testProductList = getTestProductList();
        log.debug("Test Product List: {}", testProductList);

        //when
        for (int i = 0; i < TEST_PRODUCT_COUNT; i++) {
            int productVisits = VISITS[i];
            Product product = testProductList.get(i);
            Mono<Long> insertedCount = Flux.range(0, productVisits)
                    .flatMap(idx -> productVisitService.incrementProductVisits(product))
                    .count();
            StepVerifier.create(insertedCount)
                    .expectNext((long) productVisits)
                    .verifyComplete();
        }

        log.debug("Inserted visits successfully");

        //then
        Flux<Integer> topProductIds = productVisitService
                .getTopProducts(3)
                .log()
                .flatMapIterable(Function.identity())
                .doOnNext(pr -> log.debug("top product: {}", pr))
                .map(Product::getId);

        StepVerifier
                .create(topProductIds)
                .expectNext(testProductList.get(2).getId())
                .expectNext(testProductList.get(4).getId())
                .expectNext(testProductList.get(1).getId())
                .verifyComplete();
    }

    private List<Product> getTestProductList() {

        List<Product> products = new ArrayList<>();

        Mono<List<Product>> collectList = Flux.range(1, TEST_PRODUCT_COUNT)
                .flatMap(productService::getProductById)
                .sort(Comparator.comparing(Product::getId))
                .collectList()
                .doOnNext(products::addAll);

        StepVerifier.create(collectList)
                .assertNext(productList -> assertThat(productList).isNotNull().hasSize(TEST_PRODUCT_COUNT))
                .verifyComplete();

        return products;
    }
}