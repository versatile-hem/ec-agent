package com.ek.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ek.app.security.JwtAuthenticationFilter;
import com.ek.app.security.RestAuthenticationEntryPoint;

/**
 * 
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
        SecurityFilterChain securityFilterChain(
                        HttpSecurity http,
                        JwtAuthenticationFilter jwtAuthenticationFilter,
                        RestAuthenticationEntryPoint restAuthenticationEntryPoint) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/error",
                        "/swagger/**",
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/api-docs/**",
                        "/api/auth/login")
                .permitAll()
                .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/customers/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/customers/**").hasRole("ADMIN")
                .requestMatchers("/api/stock-in/**", "/api/daily-operations/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers("/api/inventory/**").hasAnyRole("ADMIN", "OPERATION_MANAGER")
                .requestMatchers(HttpMethod.POST, "/api/sales-orders/**").hasAnyRole("ADMIN", "FIELD_SALES_EXECUTIVE")
                .requestMatchers(HttpMethod.GET, "/api/sales-orders/**").hasAnyRole("ADMIN", "FIELD_SALES_EXECUTIVE")
                .requestMatchers("/api/payments/**").hasAnyRole("ADMIN", "FIELD_SALES_EXECUTIVE")
                .requestMatchers("/api/commission/**").hasAnyRole("ADMIN", "FIELD_SALES_EXECUTIVE")
                .requestMatchers("/api/**").hasRole("ADMIN")
                .anyRequest().authenticated())
                                .csrf(AbstractHttpConfigurer::disable)
                                .formLogin(AbstractHttpConfigurer::disable)
                                .httpBasic(AbstractHttpConfigurer::disable)
                                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .exceptionHandling(ex -> ex.authenticationEntryPoint(restAuthenticationEntryPoint))
                                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

        @Bean
        AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
                return configuration.getAuthenticationManager();
        }

    @Bean
    public UserDetailsService users() {
        return new InMemoryUserDetailsManager(
                org.springframework.security.core.userdetails.User.withUsername("admin@nexo.com")
                        .password("{noop}admin123")
                        .roles("ADMIN")
                        .build(),
                org.springframework.security.core.userdetails.User.withUsername("gm")
                        .password("{noop}Earendel@54321")
                        .roles("OPERATION_MANAGER")
                        .build(),
                org.springframework.security.core.userdetails.User.withUsername("arif@earendelkids.com")
                        .password("{noop}admin123")
                        .roles("OPERATION_MANAGER", "FIELD_SALES_EXECUTIVE")
                        .build()

        );
    }

}
