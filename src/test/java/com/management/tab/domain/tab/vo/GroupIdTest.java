package com.management.tab.domain.tab.vo;

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
class GroupIdTest {

    @ParameterizedTest(name = "{0}로 GroupId를 초기화할 수 있다")
    @ValueSource(longs = {1L, 10L, 100L, 1000L, Long.MAX_VALUE})
    void 유효한_양수로_GroupId를_초기화할_수_있다(Long value) {
        // when
        GroupId groupId = GroupId.create(value);

        // then
        assertAll(
                () -> assertThat(groupId).isNotNull(),
                () -> assertThat(groupId.getValue()).isEqualTo(value)
        );
    }

    @Test
    void null로는_GroupId를_초기화할_수_없다() {
        // given
        Long value = null;

        // when & then
        assertThatThrownBy(() -> GroupId.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("GroupId는 양수여야 합니다");
    }

    @ParameterizedTest(name = "{0}일 때 GroupId를 초기화할 수 없다")
    @ValueSource(longs = {-1, 0})
    void 음수나_0으로는_GroupId를_초기화할_수_없다(Long value) {
        // when & then
        assertThatThrownBy(() -> GroupId.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("GroupId는 양수여야 합니다");
    }

    @Test
    void 같은_값을_가진_GroupId는_동등하다() {
        // given
        GroupId groupId1 = GroupId.create(1L);
        GroupId groupId2 = GroupId.create(1L);

        // when & then
        assertAll(
                () -> assertThat(groupId1).isEqualTo(groupId2),
                () -> assertThat(groupId1).hasSameHashCodeAs(groupId2)
        );
    }

    @Test
    void 다른_값을_가진_GroupId는_동등하지_않다() {
        // given
        GroupId groupId1 = GroupId.create(1L);
        GroupId groupId2 = GroupId.create(2L);

        // when & then
        assertAll(
                () -> assertThat(groupId1).isNotEqualTo(groupId2),
                () -> assertThat(groupId1).doesNotHaveSameHashCodeAs(groupId2)
        );
    }
}
