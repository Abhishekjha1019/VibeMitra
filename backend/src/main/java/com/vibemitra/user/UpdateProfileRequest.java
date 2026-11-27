package com.vibemitra.user;

import com.vibemitra.auth.City;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Partial profile update. Null fields are left unchanged. The date of birth and email are
 * deliberately not editable here: the age gate is set at registration.
 */
public record UpdateProfileRequest(
        @Size(min = 2, max = 60)
        @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Display name contains invalid characters")
        String displayName,

        City city
) {
}
