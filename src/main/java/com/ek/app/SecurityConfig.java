package com.ek.app;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.ek.app.gui.LoginView;
import com.vaadin.flow.spring.security.VaadinWebSecurity;

/**
 * 
 */
@EnableWebSecurity
@Configuration
public class SecurityConfig extends VaadinWebSecurity {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
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
