package com.ek.app;

import java.util.Optional;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.context.SecurityContextHolder;

@SpringBootApplication
@ComponentScan(basePackages = "com.ek.app",
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX,
        pattern = "com\\.ek\\.app\\.productcatalog\\.(ProductController|ProductGraphQLController|ProductServiceImpl)"))
@EntityScan(basePackages = {
    "com.ek.app.billing.infra.db",
    "com.ek.app.inventory.infra.db",
    "com.ek.app.productcatalog.infra.db"
})
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class FlipkartLabelAgentApplication {
    public static void main(String[] args) {
        SpringApplication.run(FlipkartLabelAgentApplication.class, args);
    }

    
    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(auth -> auth.isAuthenticated())
            .map(auth -> auth.getName())
            .filter(name -> name != null && !name.isBlank() && !"anonymousUser".equalsIgnoreCase(name))
            .or(() -> Optional.of("system"));
    }

}
