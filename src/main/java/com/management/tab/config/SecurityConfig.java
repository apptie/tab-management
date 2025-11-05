package com.management.tab.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.management.tab.application.auth.GenerateTokenService;
import com.management.tab.application.auth.LoginService;
import com.management.tab.config.auth.security.core.OAuth2UserDetailsService;
import com.management.tab.config.auth.security.filter.OAuth2AuthenticationFilter;
import com.management.tab.config.auth.security.filter.OAuth2RegistrationValidateFilter;
import com.management.tab.config.auth.security.handler.OAuth2AccessDeniedHandler;
import com.management.tab.config.auth.security.handler.OAuth2AuthenticationEntryPoint;
import com.management.tab.config.auth.security.handler.OAuth2AuthenticationFailureHandler;
import com.management.tab.config.auth.security.handler.OAuth2SuccessHandler;
import com.management.tab.config.properties.TokenProperties;
import com.management.tab.domain.auth.TokenDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestRedirectFilter;
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final ObjectMapper objectMapper;
    private final TokenProperties tokenProperties;
    private final TokenDecoder tokenDecoder;
    private final LoginService loginService;
    private final GenerateTokenService generateTokenService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Bean
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                    .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers(HttpMethod.GET, "/*.html").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/groups/**").permitAll()
                    .requestMatchers(HttpMethod.GET, "/api/tabs/groups/{groupId}/tree").permitAll()
                    .anyRequest().authenticated()
            )
            .exceptionHandling(handler -> handler
                    .authenticationEntryPoint(oAuth2AuthenticationEntryPoint())
                    .accessDeniedHandler(oAuth2AccessDeniedHandler())
            )
            .oauth2Login(oauth -> oauth
                    .successHandler(oAuth2SuccessHandler())
                    .failureHandler(oAuth2AuthenticationFailureHandler())
            )
            .addFilterBefore(oAuth2AuthenticationFilter(), OAuth2LoginAuthenticationFilter.class)
            .addFilterBefore(oAuth2RegistrationValidateFilter(), OAuth2AuthorizationRequestRedirectFilter.class);

        return http.build();
    }

    @Bean
    public OAuth2AuthenticationEntryPoint oAuth2AuthenticationEntryPoint() {
        return new OAuth2AuthenticationEntryPoint(objectMapper);
    }

    @Bean
    public OAuth2AccessDeniedHandler oAuth2AccessDeniedHandler() {
        return new OAuth2AccessDeniedHandler(objectMapper);
    }

    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProperties, loginService, generateTokenService);
    }

    @Bean
    public OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler() {
        return new OAuth2AuthenticationFailureHandler(objectMapper);
    }

    @Bean
    public OAuth2AuthenticationFilter oAuth2AuthenticationFilter() {
        return new OAuth2AuthenticationFilter(oAuth2UserDetailsService());
    }

    @Bean
    public OAuth2UserDetailsService oAuth2UserDetailsService() {
        return new OAuth2UserDetailsService(tokenDecoder);
    }

    @Bean
    public OAuth2RegistrationValidateFilter oAuth2RegistrationValidateFilter() {
        return new OAuth2RegistrationValidateFilter(objectMapper, handlerExceptionResolver);
    }
}
