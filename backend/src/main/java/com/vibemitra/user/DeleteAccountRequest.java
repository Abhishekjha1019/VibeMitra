package com.vibemitra.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** Password confirmation for account deletion. */
public record DeleteAccountRequest(
        @NotBlank @Size(max = 72) String password
) {
}
