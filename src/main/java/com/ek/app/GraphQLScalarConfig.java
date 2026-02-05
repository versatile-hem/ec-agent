package com.ek.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;

import graphql.scalars.ExtendedScalars;

@Configuration
public class GraphQLScalarConfig {

	@Bean
	public RuntimeWiringConfigurer runtimeWiringConfigurer() {
		return wiringBuilder -> wiringBuilder.scalar(ExtendedScalars.UUID) // maps java.util.UUID
				.scalar(ExtendedScalars.DateTime); // maps java.time.Instant/OffsetDateTime
	}

}
