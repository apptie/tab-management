package com.management.tab.application.auth;

import com.management.tab.domain.user.User;
import com.management.tab.domain.user.vo.RegistrationId;
import com.management.tab.domain.user.vo.Social;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = "classpath:sql/schema.sql")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class SignUpServiceTest {

    @Autowired
    SignUpService signUpService;

    @Test
    void 소셜_정보로_새로운_사용자가_회원_가입을_할_수_있다() {
        // given
        Social social = new Social(RegistrationId.KAKAO, "kakao12345");

        // when
        User actual = signUpService.signUp(social);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getNickname()).isEqualTo("익명"),
                () -> assertThat(actual.getRegistrationId()).isEqualTo("KAKAO"),
                () -> assertThat(actual.getSocialId()).isEqualTo("kakao12345")
        );
    }
}
