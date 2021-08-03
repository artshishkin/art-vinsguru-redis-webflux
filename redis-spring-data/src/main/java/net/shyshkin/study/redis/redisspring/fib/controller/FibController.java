package net.shyshkin.study.redis.redisspring.fib.controller;

import lombok.RequiredArgsConstructor;
import net.shyshkin.study.redis.redisspring.fib.service.FibService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("fib")
@RequiredArgsConstructor
public class FibController {

    private final FibService fibService;

    @GetMapping("{index}/{name}")
    public Mono<Long> getFib(@PathVariable int index, @PathVariable String name) {
        return Mono.fromSupplier(() -> fibService.getFib(index, name));
    }

    @GetMapping("{index}/clear")
    public Mono<Void> clearCache(@PathVariable int index) {
        return Mono.fromRunnable(() -> fibService.clearCache(index));
    }
}
