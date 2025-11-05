package com.management.tab.application.auth;

import com.management.tab.application.auth.dto.LoggedInUserDto;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/service/login-service-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class LoginServiceTest {

    @Autowired
    LoginService loginService;

    @Test
    void 기존_사용자가_소셜_로그인할_수_있다() {
        // when
        LoggedInUserDto actual = loginService.login("KAKAO", "kakao12345");

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(1L),
                () -> assertThat(actual.nickname()).isEqualTo("기존 사용자"),
                () -> assertThat(actual.isSignUp()).isFalse()
        );
    }

    @Test
    void 처음_소셜_로그인하는_사용자는_회원_가입을_진행한다() {
        // when
        LoggedInUserDto actual = loginService.login("KAKAO", "kakao99999");

        // then
        assertAll(
                () -> assertThat(actual.id()).isNotNull(),
                () -> assertThat(actual.nickname()).isEqualTo("닉네임을 정해주세요."),
                () -> assertThat(actual.isSignUp()).isTrue()
        );
    }
}
