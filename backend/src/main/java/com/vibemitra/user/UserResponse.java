package com.vibemitra.user;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/** Public view of an account. Never includes the password hash. */
public record UserResponse(
        UUID id,
        String email,
        String displayName,
        LocalDate dateOfBirth,
        String city,
        String status,
        Instant createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getDisplayName(),
                user.getDateOfBirth(),
                user.getCity(),
                user.getStatus(),
                user.getCreatedAt());
    }
}
