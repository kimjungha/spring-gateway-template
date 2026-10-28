package com.jungha.spring_gateway_template.config;

import com.jungha.spring_gateway_template.predicate.PathPrefixMethodRoutePredicateFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class RouteConfig {

    private final DomainProperties domainProperties;
    private final PathPrefixMethodRoutePredicateFactory pathPrefixMethod;

    @Bean
    public RouteLocator getRoutes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-server-connect-v2", r -> r
                        .path("/users/**")
                        .and()
                        .after(ZonedDateTime.of(2026,11,1,0,0,0,0,ZoneId.of("Asia/Seoul")))
                        .uri(domainProperties.domain().get("users"))
                )
                .route("user-server-connect", r -> r
                        .path("/users/**")
                        .filters(f -> f.filter((exchange, chain) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.OK);
                            exchange.getResponse().getHeaders().add("X-Mock", "Success Gateway Test : Users Domain");
                            return exchange.getResponse().setComplete();
                        }))
                        .uri("http://example.org") // 실제로 호출되지 않음
                )
                .route("delivery-server-connect", r -> r
                        .predicate(pathPrefixMethod.apply(c -> {
                            c.setPrefix("/delivery");
                            c.setMethods(List.of(HttpMethod.GET, HttpMethod.POST));
                        }))
                        .uri(domainProperties.domain().get("delivery"))
                )
                .route("customer-server-connect", r->r
                        .path("/customer/**")
                        .uri(domainProperties.domain().get("inventory"))
                )
                .build();
    }

}
