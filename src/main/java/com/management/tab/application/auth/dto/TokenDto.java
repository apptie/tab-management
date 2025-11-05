package com.management.tab.application.auth.dto;

public record TokenDto(String accessToken, String refreshToken, String tokenScheme) {
}
