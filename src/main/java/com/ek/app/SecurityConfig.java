package com.ek.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/error",
                        "/swagger/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api-docs/**")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers("/api/stock-in/**", "/api/daily-operations/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated())
                .csrf(csrf -> csrf.disable())
                .formLogin(form -> form.disable())
                .httpBasic(httpBasic -> {
                });
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                org.springframework.security.core.userdetails.User.withUsername("admin")
                        .password("{noop}Earendel@4321")
                        .roles("ADMIN")
                        .build(),
                org.springframework.security.core.userdetails.User.withUsername("gm")
                        .password("{noop}Earendel@54321")
                        .roles("OPERATION_MANAGER")
                        .build()

        );
    }

}
