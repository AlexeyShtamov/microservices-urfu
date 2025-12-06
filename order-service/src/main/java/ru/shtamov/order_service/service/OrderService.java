package ru.shtamov.order_service.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.shtamov.order_service.model.Order;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final List<Order> orders = new ArrayList<>();

    @PostConstruct
    void seed() {
        orders.add(new Order(1L, 1L, 100L, 1, new BigDecimal("199.99")));
        orders.add(new Order(2L, 1L, 101L, 2, new BigDecimal("29.98")));
        orders.add(new Order(3L, 2L, 102L, 1, new BigDecimal("9.99")));
    }

    public List<Order> findByUser(Long userId) {
        return orders.stream()
                .filter(order -> order.getUserId().equals(userId))
                .collect(Collectors.toList());
    }
}
