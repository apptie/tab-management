package com.management.tab.infrastructure.jwt;

import com.management.tab.config.auth.security.enums.TokenType;
import com.nimbusds.jose.JWSSigner;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwsSignerFinder {

    private final JWSSigner accessTokenSigner;
    private final JWSSigner refreshTokenSigner;

    public JWSSigner findByTokenType(TokenType tokenType) {
        if (TokenType.ACCESS == tokenType) {
            return accessTokenSigner;
        }

        return refreshTokenSigner;
    }
}
