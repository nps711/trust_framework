package com.trust.common.redis.idempotency;

import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;

public class RedisIdempotencyStore implements IdempotencyStore {
    private final StringRedisTemplate redisTemplate;

    public RedisIdempotencyStore(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean markIfAbsent(String key, Duration ttl) {
        Boolean success = redisTemplate.opsForValue().setIfAbsent(key, "1", ttl);
        return Boolean.TRUE.equals(success);
    }
}
