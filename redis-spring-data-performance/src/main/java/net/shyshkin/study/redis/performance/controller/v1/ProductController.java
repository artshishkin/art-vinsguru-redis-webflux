package net.shyshkin.study.redis.performance.controller.v1;

import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.service.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("v1/products")
public class ProductController {

    private final ProductService service;

    public ProductController(@Qualifier("directDBCallProductService") ProductService service) {
        this.service = service;
    }

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable int id) {
        return service.getProductById(id);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable int id, @RequestBody Mono<Product> productMono) {
        return service.updateProduct(id, productMono);
    }

}
