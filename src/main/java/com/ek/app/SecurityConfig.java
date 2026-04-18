package com.ek.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.ek.app.gui.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

/**
 * 
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Bean
    @Order(1)
    SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/**", "/error")
            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
            .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Keep Vaadin's default security for UI routes.
        super.configure(http);

        setLoginView(http, LoginView.class);
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
                        .roles("ADMIN")
                        .build()

        );
    }

}
