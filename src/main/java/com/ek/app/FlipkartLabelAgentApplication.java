package com.ek.app;

import java.util.Optional;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class FlipkartLabelAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlipkartLabelAgentApplication.class, args);
    }

    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getName());
    }

    @Bean
public GroupedOpenApi billApi() {
    return GroupedOpenApi.builder()
            .group("billing")
            .pathsToMatch("/api/bill/**")
            .build();
}
}
