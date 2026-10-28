package com.jungha.spring_gateway_template.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import javax.crypto.SecretKey;
import org.springframework.http.HttpHeaders;

import java.nio.charset.StandardCharsets;

/**
 * Authorization : Bearer {JWT} 를 검증하는 필터
 * - 서명이 틀리거나 만료되는 경우 401
 * - 성공하면 토큰의 사용자 ID 를 X-User-Id 헤더로 백엔드서버에 전달
 */
@Slf4j
@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config>{

    private final SecretKey secretKey;
    public AuthenticationFilter(@Value("${jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public GatewayFilter apply(AuthenticationFilter.Config config) {
        return ((exchange, chain) -> {
            // 1. Authorization 헤더에서 토큰 Get
            String authorization = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            if (authorization == null || authorization.isBlank() || !authorization.startsWith("Bearer ")) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }
            // 2. token 서명 검증 + 만료 확인
            String token = authorization.substring(7);
            Claims claims;
            try {
                claims = Jwts.parser()
                        .verifyWith(secretKey) // 키 확인
                        .build() // 파서 생성
                        .parseSignedClaims(token) // 파싱 + 서명검증+ 만료 검증
                        .getPayload(); // claim get
            } catch (JwtException e) {
                log.debug("Invalid JWT:{}", e.getMessage());
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            //3. 사용자 Id 를 X-User-Id 헤더로 백엔드에 전달
            String userId = claims.getSubject();
            ServerWebExchange authExchange = exchange.mutate()
                    .request(r -> r.headers(h ->{
                        h.remove("X-User-Id");
                        h.set("X-User-Id",userId);
                    })).build();

            return chain.filter(authExchange);
        });
    }

    @Getter
    @Setter
    public static class Config{}
}
