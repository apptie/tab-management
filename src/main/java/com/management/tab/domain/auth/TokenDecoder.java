package com.management.tab.domain.auth;

import com.management.tab.config.auth.security.enums.TokenType;

public interface TokenDecoder {

    PrivateClaims decode(TokenType tokenType, String token);
}
