package ru.shtamov.apigateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class RateLimitingFilter implements WebFilter {

    private final Map<String, Window> buckets = new ConcurrentHashMap<>();

    @Value("${rate-limit.requests:30}")
    private int requestsPerWindow;

    @Value("${rate-limit.window-seconds:60}")
    private int windowSeconds;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String key = exchange.getRequest().getHeaders().getFirst("X-User-Id");
        if (key == null || key.isBlank()) {
            key = exchange.getRequest().getRemoteAddress() != null
                    ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                    : "anonymous";
        }

        Window window = buckets.computeIfAbsent(key, k -> new Window(Instant.now(), new AtomicInteger(0)));
        synchronized (window) {
            Instant now = Instant.now();
            if (now.isAfter(window.start.plusSeconds(windowSeconds))) {
                window.start = now;
                window.counter.set(0);
            }
            if (window.counter.incrementAndGet() > requestsPerWindow) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        }

        return chain.filter(exchange);
    }

    private static class Window {
        private Instant start;
        private final AtomicInteger counter;

        private Window(Instant start, AtomicInteger counter) {
            this.start = start;
            this.counter = counter;
        }
    }
}
