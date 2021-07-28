package net.shyshkin.study.redis.performance.controller;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.study.redis.performance.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@SpringBootTest
class ProductControllerTest {

    @Autowired
    ProductController productController;

    WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(productController).build();
    }

    @Test
    void getProduct() {
        //given
        int id = 100;

        //when
        await()
                .timeout(5, TimeUnit.SECONDS)
                .untilAsserted(() ->
                        webTestClient.get()
                                .uri("/products/{id}", id)
                                .exchange()

                                //then
                                .expectStatus().isOk()
                                .expectBody(Product.class)
                                .value(pr -> assertThat(pr)
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("id", id))
                );
    }

    @Test
    void updateProduct() {
        //given
        int id = 100;
        Product productToUpdate = Product.builder().price(123.00).description("New Description").build();

        //when
        await()
                .timeout(5, TimeUnit.SECONDS)
                .untilAsserted(() ->

                        webTestClient.put()
                                .uri("/products/{id}", id)
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
                                )
                );
    }
}