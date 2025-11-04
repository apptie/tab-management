package com.management.tab.domain.auth;

import com.management.tab.config.auth.security.enums.TokenType;
import java.util.Optional;

public interface TokenDecoder {

    Optional<PrivateClaims> decode(TokenType tokenType, String token);
}
