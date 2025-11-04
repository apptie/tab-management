package com.management.tab.config.auth.security.filter;

import com.management.tab.config.auth.security.core.OAuth2AuthenticationToken;
import com.management.tab.config.auth.security.core.OAuth2UserDetails;
import com.management.tab.config.auth.security.core.OAuth2UserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class OAuth2AuthenticationFilter extends OncePerRequestFilter {

    private static final String TOKEN_SCHEME = "Bearer ";

    private final OAuth2UserDetailsService oAuth2UserDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        extractToken(request).map(this::parseToken)
                             .map(oAuth2UserDetailsService::loadUserByUsername)
                             .ifPresent(this::setAuthentication);

        filterChain.doFilter(request, response);
    }

    private void setAuthentication(OAuth2UserDetails oAuth2UserDetails) {
        SecurityContextHolder.getContext()
                             .setAuthentication(
                                     new OAuth2AuthenticationToken(
                                             oAuth2UserDetails,
                                             oAuth2UserDetails.getAuthorities()
                                     )
                             );
    }

    private Optional<String> extractToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

    private String parseToken(String token) {
        validateToken(token);

        return token.substring(TOKEN_SCHEME.length());
    }

    private void validateToken(String token) {
        if (!token.startsWith(TOKEN_SCHEME)) {
            throw new InvalidTokenTypeException();
        }
    }

    public static class InvalidTokenTypeException extends IllegalArgumentException {

        public InvalidTokenTypeException() {
            super("Bearer 타입의 토큰이 아닙니다.");
        }
    }
}
