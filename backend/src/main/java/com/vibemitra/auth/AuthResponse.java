package com.vibemitra.auth;

import java.util.UUID;

public record AuthResponse(String accessToken, UUID userId) {
}
