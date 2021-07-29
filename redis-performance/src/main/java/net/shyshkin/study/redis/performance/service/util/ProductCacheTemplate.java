package net.shyshkin.study.redis.performance.service.util;

import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.repository.ProductRepository;
import org.redisson.api.RMapReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Primary
public class ProductCacheTemplate extends CacheTemplate<Integer, Product> {

    private final ProductRepository repository;
    private final RMapReactive<Integer, Product> cache;

    public ProductCacheTemplate(ProductRepository repository, RedissonReactiveClient redissonClient) {
        this.repository = repository;
        this.cache = redissonClient.getMap("products", new TypedJsonJacksonCodec(Integer.class, Product.class));
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return cache.get(id);
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
                .fastPut(id, product)
                .thenReturn(product);
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return repository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return cache.fastRemove(id).then();
    }
}
