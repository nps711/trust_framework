package com.trust.common.redis.idempotency;

import java.time.Duration;

public interface IdempotencyStore {
    boolean markIfAbsent(String key, Duration ttl);
}
