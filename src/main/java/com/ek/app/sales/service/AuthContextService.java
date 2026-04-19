package com.ek.app.sales.service;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthContextService {

    public String username() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication == null ? "system" : authentication.getName();
    }

    public Set<String> roles() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return Set.of();
        }
        return authentication.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .collect(Collectors.toSet());
    }

    public boolean isAdmin() {
        return roles().contains("ROLE_ADMIN");
    }

    public boolean isFse() {
        return roles().contains("ROLE_FIELD_SALES_EXECUTIVE");
    }
}
