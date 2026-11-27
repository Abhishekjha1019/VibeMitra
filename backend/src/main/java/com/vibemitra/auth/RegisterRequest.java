package com.vibemitra.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record RegisterRequest(
        @NotBlank @Email @Size(max = 255) String email,

        @NotBlank
        @Size(min = 8, max = 72, message = "Password must be 8–72 characters")
        // BCrypt only uses the first 72 bytes, so longer passwords are rejected rather than silently truncated.
        String password,

        @NotBlank
        @Size(min = 2, max = 60)
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Display name contains invalid characters")
        String displayName,

        @NotNull LocalDate dateOfBirth,

        @NotNull City city
) {
}
