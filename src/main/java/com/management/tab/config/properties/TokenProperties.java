package com.management.tab.config.properties;

import com.management.tab.config.auth.security.enums.TokenType;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("token")
public record TokenProperties(
        String accessKey,
        String refreshKey,
        String issuer,
        int accessExpiredSeconds,
        int refreshExpiredSeconds,
        long accessExpiredMillisSeconds,
        long refreshExpiredMillisSeconds
) {

    public String findTokenKey(TokenType tokenType) {
        if (TokenType.ACCESS == tokenType) {
            return accessKey;
        }

        return refreshKey;
    }

    public Long findExpiredMillisSeconds(TokenType tokenType) {
        if (TokenType.ACCESS == tokenType) {
            return accessExpiredMillisSeconds;
        }

        return refreshExpiredMillisSeconds;
    }
}
