package com.ozmenyavuz.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_USER("USER"),
    ROLE_ADMIN("ADMIN"),
    ROLE_MOD("MOD"),
    ROLE_YVZ("YVZ");


    // UI veya log gÃ¶sterimi icin
    private String value;

    UserRole(String value) {
        this.value = value;
    }
    public String getValue() {
        return this.value;
    }

    @Override
    public String getAuthority() {
        return name(); // -> Spring ROLE_ prefix ile calisir
        // Spring Security, kullanıcıya ait rolleri anlamak için getAuthority() cagirir
        // Set<UserRole> → otomatik olarak Set<? extends GrantedAuthority> olur.
    }
}
