package com.example.auth_service.requests;

import com.example.auth_service.entities.UserRole;

import jakarta.validation.constraints.NotBlank;

public record RegisterRequest(@NotBlank String username, @NotBlank String email, @NotBlank String password, UserRole userRole) {
}
