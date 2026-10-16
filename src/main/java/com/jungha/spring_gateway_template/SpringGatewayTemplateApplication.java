package com.jungha.spring_gateway_template;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class SpringGatewayTemplateApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringGatewayTemplateApplication.class, args);
	}

}
