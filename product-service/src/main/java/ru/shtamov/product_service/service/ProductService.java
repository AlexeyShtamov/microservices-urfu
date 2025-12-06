package ru.shtamov.product_service.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.shtamov.product_service.model.Product;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ProductService {
    private final Map<Long, Product> products = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        products.put(100L, new Product(100L, "Headphones", new BigDecimal("199.99")));
        products.put(101L, new Product(101L, "USB cable", new BigDecimal("14.99")));
        products.put(102L, new Product(102L, "Notebook", new BigDecimal("9.99")));
    }

    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(products.get(id));
    }
}
