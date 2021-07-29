package net.shyshkin.study.redis.performance.assignment.controller.v3;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.redisson.api.RMapReactive;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest {

    @Autowired
    ProductController productController;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RedissonReactiveClient redissonClient;

    WebTestClient webTestClient;
    private RMapReactive<Integer, Product> productMap;
    private RScoredSortedSetReactive<Integer> topProducts;
    private AtomicReference<Double> initScore;
    private int id;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(productController).build();
        productMap = redissonClient.getMap("products", new TypedJsonJacksonCodec(Integer.class, Product.class));
        this.topProducts = redissonClient.getScoredSortedSet("top-products", IntegerCodec.INSTANCE);

        id = 100;
        initScore = new AtomicReference<>(0.0);
        StepVerifier
                .create(
                        topProducts
                                .getScore(id)
                                .doOnNext(score -> log.debug("Initial score for id `{}` is `{}`", id, score))
                )
                .thenConsumeWhile(score -> true, initScore::set)
                .verifyComplete();
    }

    @Test
    @Order(10)
    void getProduct() {

        //when
        await()
                .timeout(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                            webTestClient.get()
                                    .uri("/v3/products/{id}", id)
                                    .exchange()

                                    //then
                                    .expectStatus().isOk()
                                    .expectBody(Product.class)
                                    .value(pr -> assertThat(pr)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id", id));
                            StepVerifier
                                    .create(productMap.get(id).doOnNext(pr -> log.debug("Product from cache: {}", pr)))
                                    .expectNextCount(1)
                                    .verifyComplete();

                            StepVerifier
                                    .create(topProducts.getScore(id))
                                    .thenConsumeWhile(
                                            score -> true,
                                            score -> assertThat(score).isEqualTo(initScore.get() + 1)
                                    )
                                    .verifyComplete();
                        }
                );
    }

    @Test
    @Order(20)
    void updateProduct() {
        //given
        Product productToUpdate = Product.builder().price(123.00).description("New Description").build();

        //when
        await()
                .timeout(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {

                            webTestClient.put()
                                    .uri("/v3/products/{id}", id)
                                    .bodyValue(productToUpdate)
                                    .exchange()

                                    //then
                                    .expectStatus().isOk()
                                    .expectBody(Product.class)
                                    .value(pr -> assertThat(pr)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id", id)
                                            .hasFieldOrPropertyWithValue("description", "New Description")
                                            .hasFieldOrPropertyWithValue("price", 123.00)
                                    );
                            StepVerifier
                                    .create(productMap.get(id))
                                    .verifyComplete();
                        }
                );
    }

    @Test
    @Order(30)
    void deleteProduct() {
        //given
        getProduct(); //caching it

        //when
        webTestClient.delete()
                .uri("/v3/products/{id}", id)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();

        StepVerifier
                .create(productMap.get(id))
                .verifyComplete();

        StepVerifier
                .create(productRepository.existsById(id))
                .expectNext(false)
                .verifyComplete();

        StepVerifier
                .create(topProducts.getScore(id))
                .verifyComplete();
    }

}