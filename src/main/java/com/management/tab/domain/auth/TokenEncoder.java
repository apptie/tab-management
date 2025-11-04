package com.management.tab.domain.auth;

import com.management.tab.config.auth.security.enums.TokenType;
import java.time.LocalDateTime;

public interface TokenEncoder {

    String encode(LocalDateTime targetTime, TokenType tokenType, Long accountId, String roleName);
}
