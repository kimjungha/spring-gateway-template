package com.jungha.spring_gateway_template.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties("api.domain")
public class DomainProperties {
    private String delivery;
    private String customer;
}
