package com.management.tab.config.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.tab.config.auth.security.dto.response.ExceptionResponse;
import com.management.tab.domain.user.vo.RegistrationId;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@RequiredArgsConstructor
public class OAuth2RegistrationValidateFilter extends OncePerRequestFilter {

    private static final String AUTHORIZE_URI = "/login";
    private static final String REQUEST_DELIMITER = "/";

    private final ObjectMapper objectMapper;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws IOException {
        String requestURI = request.getRequestURI();

        if (requestURI.contains(AUTHORIZE_URI)) {
            String[] splitRequestUri = requestURI.split(REQUEST_DELIMITER);
            String registrationId = splitRequestUri[splitRequestUri.length - 1];

            if (RegistrationId.contains(registrationId)) {
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(StandardCharsets.UTF_8.name());
                response.setStatus(HttpStatus.BAD_REQUEST.value());

                PrintWriter writer = response.getWriter();
                ExceptionResponse exceptionResponse = new ExceptionResponse("지원하지 않는 소셜 로그인 방식입니다.");

                writer.println(objectMapper.writeValueAsString(exceptionResponse));
                return;
            }
        }

        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            handlerExceptionResolver.resolveException(request, response, null, e);
        }
    }
}
