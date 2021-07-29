package net.shyshkin.study.redis.performance.assignment.service;

import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.service.ProductService;
import org.redisson.api.RScoredSortedSetReactive;
import org.redisson.api.RedissonReactiveClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.function.Function;

@Service
public class ProductVisitServiceImpl implements ProductVisitService {

    private final RScoredSortedSetReactive<Integer> topProducts;
    private final ProductService productService;

    public ProductVisitServiceImpl(
            RedissonReactiveClient redissonClient,
            @Qualifier("cachedProductService") ProductService productService) {
        this.topProducts = redissonClient.getScoredSortedSet("top-products", IntegerCodec.INSTANCE);
        this.productService = productService;
    }

    @Override
    public Mono<List<Product>> getTopProducts(int limit) {
        return topProducts
                .valueRangeReversed(0, limit - 1)
                .flatMapIterable(Function.identity())
                .flatMapSequential(productService::getProductById)
                .collectList();
    }

    @Override
    public Mono<Product> incrementProductVisits(Product product) {
        return topProducts
                .addScore(product.getId(), 1)
                .thenReturn(product);
    }

    @Override
    public Mono<Void> deleteProductVisits(Integer id) {
        return topProducts.remove(id).then();
    }
}
