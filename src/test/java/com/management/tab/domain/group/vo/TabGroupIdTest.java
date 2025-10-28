package com.management.tab.domain.group.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabGroupIdTest {

    @Test
    void 유효한_양수로_TabGroupId를_초기화할_수_있다() {
        // when
        TabGroupId tabGroupId = TabGroupId.create(1L);

        // then
        assertAll(
                () -> assertThat(tabGroupId).isNotNull(),
                () -> assertThat(tabGroupId.getValue()).isEqualTo(1L)
        );
    }

    @Test
    void null로는_TabGroupId를_초기화할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabGroupId.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("GroupId는 양수여야 합니다");
    }

    @ParameterizedTest(name = "{0}일 때 GroupId를 초기화할 수 없다")
    @ValueSource(longs = {-1, 0})
    void 음수나_0으로는_GroupId를_초기화할_수_없다(Long value) {
        // when & then
        assertThatThrownBy(() -> TabGroupId.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("GroupId는 양수여야 합니다");
    }

    @Test
    void 같은_값을_가진_GroupId는_동등하다() {
        // given
        TabGroupId tabGroupId1 = TabGroupId.create(1L);
        TabGroupId tabGroupId2 = TabGroupId.create(1L);

        // when & then
        assertAll(
                () -> assertThat(tabGroupId1).isEqualTo(tabGroupId2),
                () -> assertThat(tabGroupId1).hasSameHashCodeAs(tabGroupId2)
        );
    }

    @Test
    void 다른_값을_가진_GroupId는_동등하지_않다() {
        // given
        TabGroupId tabGroupId1 = TabGroupId.create(1L);
        TabGroupId tabGroupId2 = TabGroupId.create(2L);

        // when & then
        assertAll(
                () -> assertThat(tabGroupId1).isNotEqualTo(tabGroupId2),
                () -> assertThat(tabGroupId1).doesNotHaveSameHashCodeAs(tabGroupId2)
        );
    }

    @Test
    void 해당_값과_동일한_id를_가졌다면_참이다() {
        // given
        TabGroupId tabGroupId = TabGroupId.create(1L);

        // when
        boolean result = tabGroupId.isEqualId(1L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 해당_값과_다른_id를_가졌다면_참이다() {
        // given
        TabGroupId tabGroupId = TabGroupId.create(1L);

        // when
        boolean result = tabGroupId.isEqualId(2L);

        // then
        assertThat(result).isFalse();
    }
}
