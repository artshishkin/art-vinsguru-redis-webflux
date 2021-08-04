package net.shyshkin.study.redis.performance.controller.v2;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.repository.ProductRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    public ReactiveRedisTemplate<String, Product> productReactiveRedisTemplate;

    @Autowired
    public ReactiveRedisTemplate<String, Integer> visitReactiveRedisTemplate;

    WebTestClient webTestClient;

    //    private RMapReactive<Integer, Product> productMap;
//    private RScoredSortedSetReactive<Integer> topProducts;
    private AtomicReference<Double> initScore;
    private int id;
    private ReactiveHashOperations<String, Integer, Product> productMap;
    private ReactiveZSetOperations<String, Integer> topProducts;
    private String TOP_PRODUCT_ZSET_KEY;
    private final String PRODUCTS_MAP_KEY = "products";


    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(productController).build();
//        productMap = redissonClient.getMap("products", new TypedJsonJacksonCodec(Integer.class, Product.class));
        this.productMap = productReactiveRedisTemplate.opsForHash();

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        this.topProducts = visitReactiveRedisTemplate.opsForZSet();
//        this.topProducts = redissonClient.getScoredSortedSet("products:visit:" + date, IntegerCodec.INSTANCE);

        id = 100;
        initScore = new AtomicReference<>(0.0);
        TOP_PRODUCT_ZSET_KEY = "products:visit:" + date;
        StepVerifier
                .create(
                        topProducts
                                .score(TOP_PRODUCT_ZSET_KEY, id)
                                .doOnNext(score -> log.debug("Initial score for id `{}` is `{}`", id, score))
                )
                .thenConsumeWhile(score -> true, initScore::set)
                .verifyComplete();
    }

    @Test
    @Order(10)
    void getProduct() {
        //given
        int id = 100;

        //when
        await()
                .timeout(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                            webTestClient.get()
                                    .uri("/v2/products/{id}", id)
                                    .exchange()

                                    //then
                                    .expectStatus().isOk()
                                    .expectBody(Product.class)
                                    .value(pr -> assertThat(pr)
                                            .hasNoNullFieldsOrProperties()
                                            .hasFieldOrPropertyWithValue("id", id));
                            StepVerifier
                                    .create(productMap.get(PRODUCTS_MAP_KEY, id).doOnNext(pr -> log.debug("Product from cache: {}", pr)))
                                    .expectNextCount(1)
                                    .verifyComplete();

                            StepVerifier
                                    .create(topProducts.score(TOP_PRODUCT_ZSET_KEY, id))
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
        int id = 100;
        Product productToUpdate = Product.builder().price(123.00).description("New Description").build();

        //when
        await()
                .timeout(5, TimeUnit.SECONDS)
                .untilAsserted(() -> {

                            webTestClient.put()
                                    .uri("/v2/products/{id}", id)
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
                                    .create(productMap.get(PRODUCTS_MAP_KEY, id))
                                    .verifyComplete();
                        }
                );
    }

    @Test
    @Order(30)
    void deleteProduct() {
        //given
        int id = 100;
        getProduct(); //caching it

        //when
        webTestClient.delete()
                .uri("/v2/products/{id}", id)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody()
                .isEmpty();

        StepVerifier
                .create(productMap.get(PRODUCTS_MAP_KEY, id))
                .verifyComplete();

        StepVerifier
                .create(productRepository.existsById(id))
                .expectNext(false)
                .verifyComplete();
    }
}