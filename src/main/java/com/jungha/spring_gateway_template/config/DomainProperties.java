package com.jungha.spring_gateway_template.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.net.URI;
import java.util.Map;

@ConfigurationProperties("api")
public record DomainProperties(Map<String, URI> domain) {
}