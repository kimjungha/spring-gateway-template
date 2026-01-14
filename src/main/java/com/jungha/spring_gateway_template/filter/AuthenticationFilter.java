package com.jungha.spring_gateway_template.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);    // Config.class 설정 객체로 사용
    }
    @Override
    public GatewayFilter apply(AuthenticationFilter.Config config) {
        return null;
    }

    // YAML 로부터 받을 설정값
    public static class Config {

    }
}
