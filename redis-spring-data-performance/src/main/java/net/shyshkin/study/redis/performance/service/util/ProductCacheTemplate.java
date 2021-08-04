package net.shyshkin.study.redis.performance.service.util;

import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.repository.ProductRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Primary
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {

    public static final String PRODUCTS_MAP = "products";
    private final ProductRepository repository;
    private final ReactiveHashOperations<String, Integer, Product> cache;
    //    private final RMapReactive<Integer, Product> cache;

    public ProductCacheTemplate(ProductRepository repository, ReactiveRedisTemplate<String, Product> productReactiveRedisTemplate) {
        this.repository = repository;
        this.cache = productReactiveRedisTemplate.opsForHash();
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return cache.get(PRODUCTS_MAP, id);
    }

    @Override
    protected Mono<Product> updateSource(Integer id, Product product) {
        return repository
                .findById(id)
                .doOnNext(pr -> product.setId(id))
                .flatMap(pr -> repository.save(product));
    }

    @Override
    protected Mono<Product> updateCache(Integer id, Product product) {
        product.setId(id);
        return cache
                .put(PRODUCTS_MAP, id, product)
                .thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return repository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return cache.remove(PRODUCTS_MAP, id).then();
    }
}
