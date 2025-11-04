package com.management.tab.application.auth;

import com.management.tab.application.auth.dto.TokenDto;
import com.management.tab.config.auth.security.enums.TokenScheme;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class GenerateTokenServiceTest {

    @Autowired
    GenerateTokenService generateTokenService;

    @Test
    void 유효한_userId로_토큰을_생성할_수_있다() {
        // when
        TokenDto result = generateTokenService.generate(1L);

        // then
        assertAll(
                () -> assertThat(result.accessToken()).isNotNull(),
                () -> assertThat(result.refreshToken()).isNotNull(),
                () -> assertThat(result.tokenScheme()).isEqualTo(TokenScheme.BEARER.name())
        );
    }
}
