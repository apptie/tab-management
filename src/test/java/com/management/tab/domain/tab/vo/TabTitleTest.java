package com.management.tab.domain.tab.vo;

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
class TabTitleTest {

    @Test
    void 유효한_문자열로_TabTitle을_초기화할_수_있다() {
        // given
        String value = "테스트 탭";

        // when
        TabTitle tabTitle = TabTitle.create(value);

        // then
        assertAll(
                () -> assertThat(tabTitle).isNotNull(),
                () -> assertThat(tabTitle.getValue()).isEqualTo("테스트 탭")
        );
    }

    @ParameterizedTest(name = "{0}일 때 초기화에 실패한다")
    @NullAndEmptySource
    void 유효하지_않은_문자열으로는_TabTitle을_초기화할_수_없다(String value) {
        // when & then
        assertThatThrownBy(() -> TabTitle.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭 제목은 비어있을 수 없습니다.");
    }

    @Test
    void 최대_길이를_초과하는_문자열로는_TabTitle을_초기화할_수_없다() {
        // given
        String value = "a".repeat(51);

        // when & then
        assertThatThrownBy(() -> TabTitle.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭 제목은 50자를 초과할 수 없습니다.");
    }

    @Test
    void 같은_값을_가진_TabTitle은_동등하다() {
        // given
        TabTitle tabTitle1 = TabTitle.create("테스트");
        TabTitle tabTitle2 = TabTitle.create("테스트");

        // when & then
        assertAll(
                () -> assertThat(tabTitle1).isEqualTo(tabTitle2),
                () -> assertThat(tabTitle1).hasSameHashCodeAs(tabTitle2)
        );
    }

    @Test
    void 다른_값을_가진_TabTitle은_동등하지_않다() {
        // given
        TabTitle tabTitle1 = TabTitle.create("테스트1");
        TabTitle tabTitle2 = TabTitle.create("테스트2");

        // when & then
        assertAll(
                () -> assertThat(tabTitle1).isNotEqualTo(tabTitle2),
                () -> assertThat(tabTitle1).doesNotHaveSameHashCodeAs(tabTitle2)
        );
    }
}
