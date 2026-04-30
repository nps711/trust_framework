package com.trust.gateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    private static final String HEADER_USER_ID = "x-user-id";
    private static final String HEADER_TRACE_ID = "x-trace-id";
    private static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String traceId = request.getHeaders().getFirst(HEADER_TRACE_ID);
        if (traceId == null || traceId.isBlank()) {
            traceId = UUID.randomUUID().toString().replace("-", "");
        }

        String userId = request.getHeaders().getFirst(HEADER_USER_ID);
        String authToken = request.getHeaders().getFirst(HEADER_AUTHORIZATION);

        ServerHttpRequest mutatedRequest = request.mutate()
                .header(HEADER_TRACE_ID, traceId)
                .header(HEADER_USER_ID, userId != null ? userId : "")
                .header(HEADER_AUTHORIZATION, authToken != null ? authToken : "")
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
