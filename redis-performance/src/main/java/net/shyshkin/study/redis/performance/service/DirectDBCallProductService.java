package net.shyshkin.study.redis.performance.service;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.repository.ProductRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class DirectDBCallProductService implements ProductService {

    private final ProductRepository repository;

    @Override
    public Mono<Product> getProductById(int id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Product> updateProduct(int id, Mono<Product> productMono) {
        return repository
                .findById(id)
                .flatMap(p -> productMono.doOnNext(pr -> pr.setId(id)))
                .flatMap(repository::save);
    }

    @Override
    public Mono<Void> deleteProduct(int id) {
        return repository.deleteById(id);
    }

}
