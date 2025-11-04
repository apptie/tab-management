package com.management.tab.config.auth.security.dto.response;

public record LoginResponse(String accessToken, String tokenScheme, boolean isSignUp) {
}
