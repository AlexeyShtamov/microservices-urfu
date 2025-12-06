package ru.shtamov.api_gateway.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter implements WebFilter {
    private static final int MAX_REQUESTS = 30;
    private static final Duration WINDOW = Duration.ofSeconds(60);

    private final Map<String, Window> counters = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String key = request.getRemoteAddress() != null ? request.getRemoteAddress().getAddress().getHostAddress() : "anonymous";
        if (request.getURI().getPath().startsWith("/actuator")) {
            return chain.filter(exchange);
        }
        Window window = counters.computeIfAbsent(key, k -> new Window());
        synchronized (window) {
            Instant now = Instant.now();
            if (Duration.between(window.startedAt, now).compareTo(WINDOW) > 0) {
                window.startedAt = now;
                window.requests = 0;
            }
            window.requests++;
            if (window.requests > MAX_REQUESTS) {
                exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                return exchange.getResponse().setComplete();
            }
        }
        return chain.filter(exchange);
    }

    private static class Window {
        private Instant startedAt = Instant.now();
        private int requests = 0;
    }
}
