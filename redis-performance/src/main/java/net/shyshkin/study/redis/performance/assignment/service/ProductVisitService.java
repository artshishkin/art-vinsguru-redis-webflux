package net.shyshkin.study.redis.performance.assignment.service;

import net.shyshkin.study.redis.performance.entity.Product;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductVisitService {

    Mono<List<Product>> getTopProducts(int limit);

    Mono<Product> incrementProductVisits(Product product);

    Mono<Void> deleteProductVisits(Integer id);
}
