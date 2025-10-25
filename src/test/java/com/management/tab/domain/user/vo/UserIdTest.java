package com.management.tab.domain.user.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class UserIdTest {

    @Test
    void 양수_값으로_UserId를_초기화할_수_있다() {
        // when
        UserId userId = UserId.create(1L);

        // then
        assertAll(
                () -> assertThat(userId).isNotNull(),
                () -> assertThat(userId.getValue()).isEqualTo(1L)
        );
    }

    @ParameterizedTest(name = "{0}일 때 초기화에 실패한다")
    @NullSource
    @ValueSource(longs = {0, -1})
    void 유효하지_않은_값으로는_UserId를_초기화할_수_없다(Long id) {
        // when & then
        assertThatThrownBy(() -> UserId.create(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("UserId는 양수여야 합니다.");
    }

    @Test
    void 같은_값을_가진_UserId는_동등하다() {
        // given
        UserId userId1 = UserId.create(1L);
        UserId userId2 = UserId.create(1L);

        // when & then
        assertAll(
                () -> assertThat(userId1).isEqualTo(userId2),
                () -> assertThat(userId1).hasSameHashCodeAs(userId2)
        );
    }

    @Test
    void 다른_값을_가진_UserId는_동등하지_않다() {
        // given
        UserId userId1 = UserId.create(1L);
        UserId userId2 = UserId.create(2L);

        // when & then
        assertAll(
                () -> assertThat(userId1).isNotEqualTo(userId2),
                () -> assertThat(userId1).doesNotHaveSameHashCodeAs(userId2)
        );
    }
}
