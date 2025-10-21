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

    @ParameterizedTest(name = "{0}로 GroupId를 초기화할 수 있다")
    @ValueSource(longs = {1L, 10L, 100L, 1000L, Long.MAX_VALUE})
    void 유효한_양수로_GroupId를_초기화할_수_있다(Long value) {
        // when
        TabGroupId tabGroupId = TabGroupId.create(value);

        // then
        assertAll(
                () -> assertThat(tabGroupId).isNotNull(),
                () -> assertThat(tabGroupId.getValue()).isEqualTo(value)
        );
    }

    @Test
    void null로는_GroupId를_초기화할_수_없다() {
        // given
        Long value = null;

        // when & then
        assertThatThrownBy(() -> TabGroupId.create(value))
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
}
