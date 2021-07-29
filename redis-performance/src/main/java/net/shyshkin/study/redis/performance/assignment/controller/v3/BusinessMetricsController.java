package net.shyshkin.study.redis.performance.assignment.controller.v3;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.performance.entity.Product;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequestMapping("trends")
@RequiredArgsConstructor
public class BusinessMetricsController {

    private final Flux<List<Product>> productBroadcast;

    @GetMapping(value = "broadcast", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Product>> getProductUpdates() {
        return productBroadcast;
    }
}
