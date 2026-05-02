package com.ek.app;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

/**
 * Test configuration for Spring Boot integration tests
 */
@TestConfiguration
public class TestConfig {

    /**
     * Provides mock AuditorAware for test context
     * This is required by entities using @CreatedBy, @LastModifiedBy annotations
     */
    @Bean
    @Primary
    public AuditorAware<String> auditorAware() {
        return () -> Optional.of("testuser@example.com");
    }
}
