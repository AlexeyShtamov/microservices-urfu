package ru.shtamov.apigateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import ru.shtamov.apigateway.config.ServiceProperties;
import ru.shtamov.apigateway.dto.*;

import java.time.Duration;
import java.util.List;

@Service
public class ProfileService {

    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final WebClient webClient;
    private final ServiceProperties serviceProperties;
    private final ReactiveRedisOperations<String, ProfileResponse> redisOperations;

    @Value("${cache.ttl-seconds:30}")
    private long cacheTtlSeconds;

    public ProfileService(WebClient webClient,
                          ServiceProperties serviceProperties,
                          ReactiveRedisOperations<String, ProfileResponse> redisOperations) {
        this.webClient = webClient;
        this.serviceProperties = serviceProperties;
        this.redisOperations = redisOperations;
    }

    public Mono<ProfileResponse> getProfile(String userId) {
        String cacheKey = "profile:" + userId;
        return redisOperations.opsForValue()
                .get(cacheKey)
                .flatMap(Mono::just)
                .switchIfEmpty(fetchAndCache(userId, cacheKey));
    }

    private Mono<ProfileResponse> fetchAndCache(String userId, String cacheKey) {
        Mono<ProfileResponse> payload = Mono.zip(fetchUser(userId), fetchOrdersWithProducts(userId))
                .map(tuple -> new ProfileResponse(tuple.getT1(), tuple.getT2()));

        return payload.flatMap(profile -> redisOperations.opsForValue()
                        .set(cacheKey, profile, Duration.ofSeconds(cacheTtlSeconds))
                        .thenReturn(profile))
                .onErrorResume(error -> {
                    log.warn("Failed to store profile in cache", error);
                    return payload;
                });
    }

    private Mono<UserDto> fetchUser(String userId) {
        String uri = serviceProperties.getUserServiceUrl() + "/users/" + userId;
        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(UserDto.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200)))
                .onErrorResume(ex -> {
                    log.warn("User service failed: {}", ex.getMessage());
                    return Mono.just(UserDto.fallback(userId));
                });
    }

    private Mono<List<OrderWithProductDto>> fetchOrdersWithProducts(String userId) {
        String uri = serviceProperties.getOrderServiceUrl() + "/orders/user/" + userId;
        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToFlux(OrderDto.class)
                .collectList()
                .flatMapMany(Flux::fromIterable)
                .flatMap(order -> fetchProduct(order.getProductId())
                        .map(product -> new OrderWithProductDto(order, product)))
                .collectList()
                .onErrorResume(ex -> {
                    log.warn("Order aggregation failed: {}", ex.getMessage());
                    return Mono.just(List.of());
                });
    }

    private Mono<ProductDto> fetchProduct(String productId) {
        String uri = serviceProperties.getProductServiceUrl() + "/products/" + productId;
        return webClient.get()
                .uri(uri)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .timeout(Duration.ofSeconds(3))
                .retryWhen(Retry.backoff(2, Duration.ofMillis(200)))
                .onErrorResume(ex -> {
                    log.warn("Product service failed for {}: {}", productId, ex.getMessage());
                    return Mono.just(ProductDto.fallback(productId));
                });
    }
}
