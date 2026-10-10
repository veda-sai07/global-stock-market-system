package com.example.stock_market.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class AuthDtos {

    public record RegisterRequest(
            @NotBlank @Size(min = 3, max = 30) String username,
            @NotBlank @Email String email,
            @NotBlank @Size(min = 6, message = "password must be at least 6 characters") String password) {
    }

    public record LoginRequest(@NotBlank String username, @NotBlank String password) {
    }

    public record UserResponse(Long id, String username, String email, BigDecimal balance) {
    }
}
