package com.management.tab.application.auth;

import com.management.tab.application.auth.dto.TokenDto;
import com.management.tab.config.auth.security.enums.TokenScheme;
import com.management.tab.config.auth.security.enums.TokenType;
import com.management.tab.domain.auth.TokenEncoder;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenerateTokenService {

    private final Clock clock;
    private final TokenEncoder tokenEncoder;

    public TokenDto generate(Long userId) {
        String accessToken = tokenEncoder.encode(LocalDateTime.now(clock), TokenType.ACCESS, userId);
        String refreshToken = tokenEncoder.encode(LocalDateTime.now(clock), TokenType.REFRESH, userId);

        return new TokenDto(accessToken, refreshToken, TokenScheme.BEARER.name());
    }
}
