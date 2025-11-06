package com.management.tab.presentation.auth;

import com.management.tab.application.auth.GenerateTokenService;
import com.management.tab.application.auth.dto.TokenDto;
import com.management.tab.config.properties.TokenProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.server.Cookie.SameSite;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RefreshTokenController {

    private static final String REFRESH_TOKEN_COOKIE_KEY = "refreshToken";

    private final TokenProperties tokenProperties;
    private final GenerateTokenService generateTokenService;

    @PostMapping("/refresh-token")
    public ResponseEntity<Void> refreshToken(HttpServletRequest request) {
        String refreshToken = findRefreshToken(request.getCookies()).orElseThrow(RefreshTokenNotFoundException::new);

        TokenDto tokenDto = generateTokenService.refreshToken(refreshToken);
        HttpCookie accessTokenCookie = createCookie("accessToken", tokenDto.accessToken());
        HttpCookie refreshTokenCookie = createCookie(REFRESH_TOKEN_COOKIE_KEY, tokenDto.refreshToken());

        return ResponseEntity.ok()
                             .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                             .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                             .build();
    }

    private Optional<String> findRefreshToken(Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (REFRESH_TOKEN_COOKIE_KEY.equals(cookie.getName())) {
                return Optional.of(cookie.getValue());
            }
        }

        return Optional.empty();
    }

    private HttpCookie createCookie(String key, String token) {
        return ResponseCookie.from(key, token)
                             .httpOnly(false)
                             .secure(false)
                             .sameSite(SameSite.LAX.name())
                             .maxAge(tokenProperties.refreshExpiredSeconds())
                             .build();
    }

    public static class RefreshTokenNotFoundException extends IllegalArgumentException {

        public RefreshTokenNotFoundException() {
            super("Cookie에서 refreshToken을 찾을 수 없습니다.");
        }
    }
}
