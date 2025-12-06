package ru.shtamov.user_service.service;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import ru.shtamov.user_service.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();

    @PostConstruct
    void seed() {
        users.put(1L, new User(1L, "Alice", "alice@example.com"));
        users.put(2L, new User(2L, "Bob", "bob@example.com"));
        users.put(3L, new User(3L, "Charlie", "charlie@example.com"));
    }

    public Optional<User> getUser(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    public Collection<User> getAll() {
        return users.values();
    }
}
