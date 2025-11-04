package com.management.tab.domain.user.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RegistrationIdTest {

    @Test
    void 유효한_이름으로_RegistrationId를_찾을_수_있다() {
        // when
        RegistrationId actual = RegistrationId.findBy("kakao");

        // then
        assertThat(actual).isEqualTo(RegistrationId.KAKAO);
    }

    @Test
    void 유효하지_않은_이름으로는_RegistrationId를_찾을_수_없다() {
        // when & then
        assertThatThrownBy(() -> RegistrationId.findBy("invalid"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("유효하지 않은 소셜 로그인 서비스 이름이 아닙니다.");
    }

    @Test
    void RegistrationId에_포함된_이름인지_확인할_수_있다() {
        // when
        boolean actual = RegistrationId.contains("kakao");

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void RegistrationId에_포함되지_않은_이름인지_확인할_수_있다() {
        // when
        boolean actual = RegistrationId.notContains("youtube");

        // then
        assertThat(actual).isTrue();
    }
}
