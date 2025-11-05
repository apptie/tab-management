package com.management.tab.config.auth.security.handler;

import com.management.tab.application.auth.GenerateTokenService;
import com.management.tab.application.auth.LoginService;
import com.management.tab.application.auth.dto.LoggedInUserDto;
import com.management.tab.config.properties.TokenProperties;
import com.management.tab.application.auth.dto.TokenDto;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    public static final String DOMAIN = "/";
    public static final String REFRESH_TOKEN_KEY = "refreshToken";
    public static final String ACCESS_TOKEN_KEY = "accessToken";

    private final TokenProperties tokenProperties;
    private final LoginService loginService;
    private final GenerateTokenService generateTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String socialId = (String) oAuth2User.getAttributes()
                                                     .get(StandardClaimNames.SUB);
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        LoggedInUserDto loggedInUserDto = loginService.login(registrationId, socialId);
        TokenDto tokenDto = generateTokenService.generate(loggedInUserDto.id());

        createRefreshTokenCookie(response, tokenDto.refreshToken());
        createAccessTokenCookie(response, tokenDto.accessToken());
        writeResponse(response);
    }

    private void writeResponse(HttpServletResponse response) {
        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setStatus(HttpStatus.CREATED.value());

        try {
            PrintWriter writer = response.getWriter();
            response.getWriter().write(
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head><meta charset='UTF-8'></head>" +
                            "<body>" +
                            "<script>" +
                            "  if (window.opener) {" +
                            "    window.opener.postMessage({type: 'LOGIN_SUCCESS'}, '*');" +
                            "  }" +
                            "  window.close();" +
                            "</script>" +
                            "</body>" +
                            "</html>"
            );
            writer.flush();
        } catch (IOException e) {
            throw new InvalidResponseWriteException(e);
        }
    }

    private void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie cookie = new Cookie(REFRESH_TOKEN_KEY, URLEncoder.encode(refreshToken, StandardCharsets.UTF_8));

        cookie.setSecure(false);
        cookie.setMaxAge(tokenProperties.accessExpiredSeconds());
        cookie.setPath(DOMAIN);
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }

    private void createAccessTokenCookie(HttpServletResponse response, String accessToken) {
        Cookie cookie = new Cookie(ACCESS_TOKEN_KEY, URLEncoder.encode(accessToken, StandardCharsets.UTF_8));

        cookie.setSecure(false);
        cookie.setMaxAge(tokenProperties.accessExpiredSeconds());
        cookie.setPath(DOMAIN);
        cookie.setAttribute("SameSite", "Lax");

        response.addCookie(cookie);
    }

    public static class InvalidResponseWriteException extends AuthenticationException {

        public InvalidResponseWriteException(Throwable e) {
            super("응답 메시지를 추가하는 과정에 문제가 발생했습니다.", e);
        }
    }
}
