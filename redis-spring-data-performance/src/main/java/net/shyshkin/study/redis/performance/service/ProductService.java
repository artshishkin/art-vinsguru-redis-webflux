package net.shyshkin.study.redis.performance.service;

import net.shyshkin.study.redis.performance.entity.Product;
import reactor.core.publisher.Mono;

public interface ProductService {
    Mono<Product> getProductById(int id);

    Mono<Product> updateProduct(int id, Mono<Product> productMono);

    Mono<Void> deleteProduct(int id);
}
