package com.jungha.spring_gateway_template.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpStatus;

@Configuration
@RequiredArgsConstructor
public class GatewayRouteConfig {

    private final DomainProperties domainProperties;

    @Bean
    public RouteLocator gatewayRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service-mock", r -> r
                        .path("/users/**")
                        .filters(f -> f.filter((exchange, chain) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.OK);
                            exchange.getResponse().getHeaders().add("X-Mock", "gateway-routing-test");
                            return exchange.getResponse().setComplete();
                        }))
                        .uri("http://example.org") // 실제로 호출되지 않음
                )
                .route("delivery-service", r -> r
                        .path("/delivery/**")
                        .uri(domainProperties.getDelivery())
                )
                .route("customer-service", r->r
                        .path("/customer/**")
                        .uri(domainProperties.getCustomer())
                )
                .build();
    }

}
