package ru.shtamov.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import ru.shtamov.apigateway.dto.ProfileResponse;

@Configuration
public class RedisConfig {

    @Bean
    public ReactiveRedisOperations<String, ProfileResponse> redisOperations(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext<String, ProfileResponse> context = RedisSerializationContext
                .<String, ProfileResponse>newSerializationContext()
                .key(org.springframework.data.redis.serializer.RedisSerializer.string())
                .value(new GenericJackson2JsonRedisSerializer())
                .build();
        return new ReactiveRedisTemplate<>(factory, context);
    }
}
