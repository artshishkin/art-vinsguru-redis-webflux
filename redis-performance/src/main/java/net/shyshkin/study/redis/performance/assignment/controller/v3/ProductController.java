package net.shyshkin.study.redis.performance.assignment.controller.v3;

import net.shyshkin.study.redis.performance.assignment.service.ProductVisitService;
import net.shyshkin.study.redis.performance.entity.Product;
import net.shyshkin.study.redis.performance.service.ProductService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController("productControllerV3")
@RequestMapping("v3/products")
public class ProductController {

    private final ProductService service;
    private final ProductVisitService productVisitService;

    public ProductController(
            @Qualifier("cachedProductService") ProductService service,
            ProductVisitService productVisitService) {
        this.service = service;
        this.productVisitService = productVisitService;
    }

    @GetMapping("{id}")
    public Mono<Product> getProduct(@PathVariable int id) {
        return service
                .getProductById(id)
                .flatMap(productVisitService::incrementProductVisits);
    }

    @PutMapping("{id}")
    public Mono<Product> updateProduct(@PathVariable int id, @RequestBody Mono<Product> productMono) {
        return service.updateProduct(id, productMono);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteProduct(@PathVariable int id) {
        return service
                .deleteProduct(id)
                .then(productVisitService.deleteProductVisits(id));
    }

}
