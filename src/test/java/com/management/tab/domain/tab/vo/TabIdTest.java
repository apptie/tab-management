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
class TabIdTest {

    @Test
    void 양수_값으로_TabId를_초기화할_수_있다() {
        // given
        Long value = 1L;

        // when
        TabId tabId = TabId.create(value);

        // then
        assertAll(
                () -> assertThat(tabId).isNotNull(),
                () -> assertThat(tabId.getValue()).isEqualTo(1L),
                () -> assertThat(tabId.getValue()).isEqualTo(1L)
        );
    }

    @Test
    void null_값은_TabId_초기화에_실패한다() {
        // given
        Long value = null;

        // when & then
        assertThatThrownBy(() -> TabId.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TabId는 양수여야 합니다.");
    }

    @ParameterizedTest(name = "{0}일 때 초기화에 실패한다")
    @ValueSource(longs = {0, -1})
    void 유효하지_않은_값으로는_TabId_초기화에_실패한다(Long id) {
        // when & then
        assertThatThrownBy(() -> TabId.create(id))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("TabId는 양수여야 합니다.");
    }

    @Test
    void 같은_값을_가진_TabId는_동등하다() {
        // given
        TabId tabId1 = TabId.create(1L);
        TabId tabId2 = TabId.create(1L);

        // when & then
        assertAll(
                () -> assertThat(tabId1).isEqualTo(tabId2),
                () -> assertThat(tabId1).hasSameHashCodeAs(tabId2)
        );
    }

    @Test
    void 다른_값을_가진_TabId는_동등하지_않다() {
        // given
        TabId tabId1 = TabId.create(1L);
        TabId tabId2 = TabId.create(2L);

        // when & then
        assertAll(
                () -> assertThat(tabId1).isNotEqualTo(tabId2),
                () -> assertThat(tabId1).doesNotHaveSameHashCodeAs(tabId2)
        );
    }

    @Test
    void id_메서드는_생성_시_전달한_값을_반환한다() {
        // given
        Long value = 100L;
        TabId tabId = TabId.create(value);

        // when
        Long result = tabId.getValue();

        // then
        assertThat(result).isEqualTo(value);
    }
}
