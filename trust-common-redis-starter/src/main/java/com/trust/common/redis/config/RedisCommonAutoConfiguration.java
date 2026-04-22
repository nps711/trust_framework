package com.trust.common.redis.config;

import com.trust.common.redis.idempotency.IdempotencyStore;
import com.trust.common.redis.idempotency.InMemoryIdempotencyStore;
import com.trust.common.redis.idempotency.RedisIdempotencyStore;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;

@AutoConfiguration
public class RedisCommonAutoConfiguration {

    @Bean
    @ConditionalOnBean(StringRedisTemplate.class)
    public IdempotencyStore redisBackedIdempotencyStore(StringRedisTemplate redisTemplate) {
        return new RedisIdempotencyStore(redisTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(IdempotencyStore.class)
    public IdempotencyStore inMemoryIdempotencyStore() {
        return new InMemoryIdempotencyStore();
    }
}
