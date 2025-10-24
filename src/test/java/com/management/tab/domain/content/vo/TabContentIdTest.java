package com.management.tab.domain.content.vo;

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
class TabContentIdTest {

    @Test
    void 양수_값으로_TabContentId를_초기화할_수_있다() {
        // given
        Long value = 1L;

        // when
        TabContentId tabContentId = TabContentId.create(value);

        // then
        assertAll(
                () -> assertThat(tabContentId).isNotNull(),
                () -> assertThat(tabContentId.getValue()).isEqualTo(1L)
        );
    }

    @Test
    void null_값은_TabContentId_초기화에_실패한다() {
        // given
        Long value = null;

        // when & then
        assertThatThrownBy(() -> TabContentId.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TabContentId는 null일 수 없습니다.");
    }

    @ParameterizedTest(name = "{0}일 때 초기화에 실패한다")
    @ValueSource(longs = {0, -1})
    void 유효하지_않은_값으로는_TabContentId_초기화에_실패한다(Long id) {
        // when & then
        assertThatThrownBy(() -> TabContentId.create(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TabContentId는 양수여야 합니다.");
    }

    @Test
    void 같은_값을_가진_TabContentId는_동등하다() {
        // given
        TabContentId tabContentId1 = TabContentId.create(1L);
        TabContentId tabContentId2 = TabContentId.create(1L);

        // when & then
        assertAll(
                () -> assertThat(tabContentId1).isEqualTo(tabContentId2),
                () -> assertThat(tabContentId1).hasSameHashCodeAs(tabContentId2)
        );
    }

    @Test
    void 다른_값을_가진_TabContentId는_동등하지_않다() {
        // given
        TabContentId tabContentId1 = TabContentId.create(1L);
        TabContentId tabContentId2 = TabContentId.create(2L);

        // when & then
        assertAll(
                () -> assertThat(tabContentId1).isNotEqualTo(tabContentId2),
                () -> assertThat(tabContentId1).doesNotHaveSameHashCodeAs(tabContentId2)
        );
    }
}
