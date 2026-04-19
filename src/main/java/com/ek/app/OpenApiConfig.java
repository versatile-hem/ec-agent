package com.ek.app;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI().info(new Info().title("EC Agent API").version("1.0.0")
				.description("REST API documentation for EC Agent backend"));
	}

	@Bean
	public GroupedOpenApi allApi() {
		return GroupedOpenApi.builder()
				.group("all")
				.pathsToMatch("/api/**")
				.build();
	}

	@Bean
	public GroupedOpenApi billingApi() {
		return GroupedOpenApi.builder()
				.group("billing")
				.pathsToMatch("/api/billing/**", "/api/invoice/**", "/api/client/**")
				.build();
	}

	@Bean
	public GroupedOpenApi inventoryApi() {
		return GroupedOpenApi.builder()
				.group("inventory")
				.pathsToMatch("/api/inventory/**", "/api/v1/inventory/**")
				.build();
	}

	@Bean
	public GroupedOpenApi productsApi() {
		return GroupedOpenApi.builder()
				.group("products")
				.pathsToMatch("/api/products/**")
				.build();
	}

	@Bean
	public GroupedOpenApi operationsApi() {
		return GroupedOpenApi.builder()
				.group("operations")
				.pathsToMatch("/api/stock-in/**", "/api/daily-operations/**")
				.build();
	}

}
