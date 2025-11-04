package com.management.tab.infrastructure.jwt;

import com.management.tab.config.auth.security.enums.TokenType;
import com.nimbusds.jose.JWSVerifier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwsVerifierFinder {

    private final JWSVerifier accessTokenJwsVerifier;
    private final JWSVerifier refreshTokenJwsVerifier;

    public JWSVerifier findByTokenType(TokenType tokenType) {
        if (TokenType.ACCESS == tokenType) {
            return accessTokenJwsVerifier;
        }

        return refreshTokenJwsVerifier;
    }
}
