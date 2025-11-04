package com.management.tab.config.auth.security.service.dto;

public record LoggedInUserDto(Long id, String nickname, boolean isSignUp) {
}
