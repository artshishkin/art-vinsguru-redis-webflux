package net.shyshkin.study.redis.performance.repository;

import net.shyshkin.study.redis.performance.entity.Product;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface ProductRepository extends ReactiveSortingRepository<Product, Integer> {
}
