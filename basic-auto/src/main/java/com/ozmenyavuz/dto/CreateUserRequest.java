package com.ozmenyavuz.dto;


import com.ozmenyavuz.model.UserRole;
import lombok.Builder;

import java.util.Set;

@Builder
public record CreateUserRequest (
        String name,
        String username,
        String password,
        Set<UserRole> authorities
){
}