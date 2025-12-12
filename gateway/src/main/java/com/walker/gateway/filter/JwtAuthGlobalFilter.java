package com.walker.gateway.filter;

import com.walker.gateway.security.JwtVerifier;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthGlobalFilter implements GlobalFilter, Ordered {

    private final JwtVerifier jwtVerifier;

    // 白名单：不需要 token 的接口
    private final List<String> whiteList = List.of(
            "/auth/**",           // 登录/注册相关全部放行
            "/actuator/**"        // 可选：放行监控
    );

    private final AntPathMatcher matcher = new AntPathMatcher();

    public JwtAuthGlobalFilter(JwtVerifier jwtVerifier) {
        this.jwtVerifier = jwtVerifier;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // 1) 白名单放行
        if (isWhite(path)) {
            return chain.filter(exchange);
        }

        // 2) 取 Authorization: Bearer xxx
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return unauthorized(exchange, "Missing token");
        }

        String token = authHeader.substring(7).trim();
        if (token.isEmpty()) {
            return unauthorized(exchange, "Empty token");
        }

        // 3) 解析 JWT
        Claims claims;
        try {
            claims = jwtVerifier.parse(token);
        } catch (JwtException e) {
            return unauthorized(exchange, "Invalid token");
        }

        // 4) 把用户信息注入到下游 header
        String userId = String.valueOf(claims.get("userId"));
        String username = claims.getSubject();

        ServerWebExchange mutated = exchange.mutate()
                .request(builder -> builder
                        .header("X-USER-ID", userId)
                        .header("X-USERNAME", username == null ? "" : username)
                )
                .build();

        return chain.filter(mutated);
    }

    private boolean isWhite(String path) {
        for (String pattern : whiteList) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange, String msg) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        // 简单返回，不写 body（你后面想统一返回 JSON，我再给你一个版本）
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100; // 越小越优先
    }
}
