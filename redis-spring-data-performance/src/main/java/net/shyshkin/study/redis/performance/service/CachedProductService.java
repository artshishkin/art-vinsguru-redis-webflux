package net.shyshkin.study.redis.performance.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.service.util.CacheTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CachedProductService implements ProductService {

    private final CacheTemplate<Integer, Product> cacheTemplate;
    private final ProductVisitService productVisitService;

    @Override
    public Mono<Product> getProductById(int id) {
        return cacheTemplate
                .get(id)
                .doFirst(() -> productVisitService.addVisit(id));
    }

    @Override
    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {

        return productMono
                .flatMap(product -> cacheTemplate.update(id, product));

    }

    @Override
    public Mono<Void> deleteProduct(int id) {
        return cacheTemplate.delete(id);
    }
}
