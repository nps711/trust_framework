package com.trust.common.redis.idempotency;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryIdempotencyStore implements IdempotencyStore {
    private final Map<String, Instant> cache = new ConcurrentHashMap<>();

    @Override
    public boolean markIfAbsent(String key, Duration ttl) {
        Instant now = Instant.now();
        cache.entrySet().removeIf(e -> e.getValue().isBefore(now));
        Instant expiresAt = now.plus(ttl);
        return cache.putIfAbsent(key, expiresAt) == null;
    }
}
