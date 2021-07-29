package net.shyshkin.study.redis.performance.service.util;

import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.repository.ProductRepository;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.codec.TypedJsonJacksonCodec;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Primary
public class ProductLocalCacheTemplate extends CacheTemplate<Integer, Product> {

    private final ProductRepository repository;
    private final RLocalCachedMap<Integer, Product> cache;

    public ProductLocalCacheTemplate(ProductRepository repository, RedissonClient redissonClient) {

        this.repository = repository;

        LocalCachedMapOptions<Integer, Product> mapOptions = LocalCachedMapOptions.<Integer, Product>defaults()
                .syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE)
                .reconnectionStrategy(LocalCachedMapOptions.ReconnectionStrategy.CLEAR);

        this.cache = redissonClient.getLocalCachedMap(
                "products",
                new TypedJsonJacksonCodec(Integer.class, Product.class),
                mapOptions
        );
    }

    @Override
    protected Mono<Product> getFromSource(Integer id) {
        return repository.findById(id);
    }

    @Override
    protected Mono<Product> getFromCache(Integer id) {
        return Mono.justOrEmpty(cache.get(id)); //no need supplier because we have map locally
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
        return Mono
                .create(sink -> cache
                        .fastPutAsync(id, product)
                        .thenAccept(b -> sink.success(product))
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        }));
    }

    @Override
    protected Mono<Void> deleteFromSource(Integer id) {
        return repository.deleteById(id);
    }

    @Override
    protected Mono<Void> deleteFromCache(Integer id) {
        return Mono
                .create(sink -> cache
                        .fastRemoveAsync(id)
                        .thenAccept(b -> sink.success())
                        .exceptionally(ex -> {
                            sink.error(ex);
                            return null;
                        }));
    }
}
