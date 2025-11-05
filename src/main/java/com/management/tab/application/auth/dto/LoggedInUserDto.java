package com.management.tab.application.auth.dto;

public record LoggedInUserDto(Long id, String nickname, boolean isSignUp) {
}
