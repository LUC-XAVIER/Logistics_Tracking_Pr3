package com.example.api_gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class JwtFilter implements GatewayFilter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${jwt.secret}")
    private String secret;

    private static final List<String> PUBLIC_URLS = List.of("auth/login", "auth/register");

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull GatewayFilterChain chain) {
        String path = exchange.getRequest().getPath().toString();
        String method = exchange.getRequest().getMethod().toString();

        System.out.println("=== JWT Filter Debug ===");
        System.out.println("Request path: " + path);
        System.out.println("Request method: " + method);
        System.out.println("Request URI: " + exchange.getRequest().getURI());

        // Allow OPTIONS requests (CORS preflight) to pass through
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("OPTIONS request detected (CORS preflight), allowing through");
            return chain.filter(exchange);
        }

        if (PUBLIC_URLS.stream().anyMatch(path::contains)) {
            System.out.println("Public URL detected, skipping JWT validation");
            return chain.filter(exchange);
        }

        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        System.out.println("Authorization header: " + authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("No valid Bearer token found");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }

        String token = authHeader.substring(7);
        System.out.println("Extracted token: " + token.substring(0, Math.min(50, token.length())) + "...");

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            System.out.println("JWT parsed successfully. Subject: " + claims.getSubject());

            // Forward user info by mutating the exchange
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header("Authorization", authHeader)
                            .header("UserEmail", claims.getSubject())
                            .build())
                    .build();

            System.out.println("Forwarding UserEmail: " + claims.getSubject());
            System.out.println("Mutated request headers: " + mutatedExchange.getRequest().getHeaders());

            return chain.filter(mutatedExchange);

        } catch (Exception e) {
            logger.error("JWT validation failed: {}", e.getMessage());
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

}