package com.example.api_gateway.config;

import org.jspecify.annotations.NonNull;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class GlobalLoggingFilter implements GlobalFilter, Ordered {

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("=== GLOBAL FILTER DEBUG ===");
        System.out.println("Request URI: " + exchange.getRequest().getURI());
        System.out.println("Request Path: " + exchange.getRequest().getPath());
        System.out.println("Request Method: " + exchange.getRequest().getMethod());
        System.out.println("Headers: " + exchange.getRequest().getHeaders());
        System.out.println("=====================================");

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -1; // High priority
    }
}

