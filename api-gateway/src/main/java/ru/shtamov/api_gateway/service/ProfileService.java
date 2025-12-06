package ru.shtamov.api_gateway.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import ru.shtamov.api_gateway.model.OrderSummary;
import ru.shtamov.api_gateway.model.ProductSummary;
import ru.shtamov.api_gateway.model.ProfileResponse;
import ru.shtamov.api_gateway.model.UserSummary;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProfileService {
    private static final Logger log = LoggerFactory.getLogger(ProfileService.class);

    private final WebClient webClient;
    private final CacheManager cacheManager;
    private final String userServiceUrl;
    private final String orderServiceUrl;
    private final String productServiceUrl;

    public ProfileService(WebClient.Builder builder,
                          CacheManager cacheManager,
                          org.springframework.beans.factory.annotation.Value("${service.user-url}") String userServiceUrl,
                          org.springframework.beans.factory.annotation.Value("${service.order-url}") String orderServiceUrl,
                          org.springframework.beans.factory.annotation.Value("${service.product-url}") String productServiceUrl) {
        this.webClient = builder.build();
        this.cacheManager = cacheManager;
        this.userServiceUrl = userServiceUrl;
        this.orderServiceUrl = orderServiceUrl;
        this.productServiceUrl = productServiceUrl;
    }

    public ProfileResponse getProfile(Long userId) {
        Cache cache = cacheManager.getCache("profiles");
        if (cache != null) {
            ProfileResponse cached = cache.get(userId, ProfileResponse.class);
            if (cached != null) {
                cached.setCached(true);
                return cached;
            }
        }

        UserSummary user = fetchUser(userId);
        List<OrderSummary> orders = fetchOrders(userId);
        for (OrderSummary order : orders) {
            ProductSummary product = fetchProduct(order.getProductId());
            order.setProduct(product);
        }

        ProfileResponse response = new ProfileResponse();
        response.setUser(user);
        response.setOrders(orders);
        response.setCached(false);

        if (cache != null) {
            cache.put(userId, response);
        }
        return response;
    }

    private UserSummary fetchUser(Long userId) {
        try {
            return webClient.get()
                    .uri(userServiceUrl + "/users/{id}", userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(UserSummary.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            throw new ResponseStatusException(e.getStatusCode(), "User not found");
        } catch (Exception e) {
            log.warn("User service unavailable, returning placeholder", e);
            UserSummary fallback = new UserSummary();
            fallback.setId(userId);
            fallback.setName("Unknown user");
            fallback.setEmail("unavailable");
            return fallback;
        }
    }

    private List<OrderSummary> fetchOrders(Long userId) {
        try {
            OrderSummary[] response = webClient.get()
                    .uri(orderServiceUrl + "/orders/user/{id}", userId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(OrderSummary[].class)
                    .block();
            if (response == null) {
                return List.of();
            }
            return Arrays.stream(response).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Order service unavailable, returning empty list", e);
            return List.of();
        }
    }

    private ProductSummary fetchProduct(Long productId) {
        try {
            return webClient.get()
                    .uri(productServiceUrl + "/products/{id}", productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(ProductSummary.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Product {} not found", productId);
            return null;
        } catch (Exception e) {
            log.warn("Product service unavailable, using fallback for product {}", productId, e);
            ProductSummary fallback = new ProductSummary();
            fallback.setId(productId);
            fallback.setName("Unavailable product");
            return fallback;
        }
    }
}
