package com.management.tab.domain.user.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NicknameTest {

    @Test
    void 유효한_문자열로_Nickname을_초기화할_수_있다() {
        // when
        Nickname nickname = Nickname.create("테스트");

        // then
        assertAll(
                () -> assertThat(nickname).isNotNull(),
                () -> assertThat(nickname.getValue()).isEqualTo("테스트")
        );
    }

    @ParameterizedTest(name = "{0}일 때 초기화에 실패한다")
    @NullAndEmptySource
    void 유효하지_않은_문자열으로는_Nickname을_초기화할_수_없다(String value) {
        // when & then
        assertThatThrownBy(() -> Nickname.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임은 비어있을 수 없습니다.");
    }

    @Test
    void 최대_길이를_초과하는_문자열로는_Nickname을_초기화할_수_없다() {
        // given
        String value = "a".repeat(11);

        // when & then
        assertThatThrownBy(() -> Nickname.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("닉네임은 10자를 초과할 수 없습니다.");
    }

    @Test
    void 같은_값을_가진_Nickname은_동등하다() {
        // given
        Nickname nickname1 = Nickname.create("테스트");
        Nickname nickname2 = Nickname.create("테스트");

        // when & then
        assertAll(
                () -> assertThat(nickname1).isEqualTo(nickname2),
                () -> assertThat(nickname1).hasSameHashCodeAs(nickname2)
        );
    }

    @Test
    void 다른_값을_가진_Nickname은_동등하지_않다() {
        // given
        Nickname nickname1 = Nickname.create("테스트1");
        Nickname nickname2 = Nickname.create("테스트2");

        // when & then
        assertAll(
                () -> assertThat(nickname1).isNotEqualTo(nickname2),
                () -> assertThat(nickname1).doesNotHaveSameHashCodeAs(nickname2)
        );
    }
}
