package com.management.tab.domain.group.vo;

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
class TabGroupNameTest {

    @Test
    void 유효한_문자열로_TabGroupName을_초기화할_수_있다() {
        // when
        TabGroupName tabGroupName = TabGroupName.create("탭 그룹");

        // then
        assertAll(
                () -> assertThat(tabGroupName).isNotNull(),
                () -> assertThat(tabGroupName.getValue()).isEqualTo("탭 그룹")
        );
    }

    @ParameterizedTest(name = "{0}일 때 TabGroupName을 초기화할 수 없다")
    @NullAndEmptySource
    void 비어_있는_TabGroupName을_초기화할_수_없다(String value) {
        // when & then
        assertThatThrownBy(() -> TabGroupName.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭 그룹 이름은 비어있을 수 없습니다.");
    }


    @Test
    void 길이_제한을_초과하는_문자열로는_TabGroupName을_초기화할_수_없다() {
        // given
        String value = "a".repeat(101);

        // when & then
        assertThatThrownBy(() -> TabGroupName.create(value))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭 그룹 이름은 100자를 초과할 수 없습니다.");
    }

    @Test
    void 같은_값을_가진_TabGroupName은_동등하다() {
        // given
        TabGroupName name1 = TabGroupName.create("탭 그룹");
        TabGroupName name2 = TabGroupName.create("탭 그룹");

        // when & then
        assertAll(
                () -> assertThat(name1).isEqualTo(name2),
                () -> assertThat(name1).hasSameHashCodeAs(name2)
        );
    }

    @Test
    void 다른_값을_가진_TabGroupName은_동등하지_않다() {
        // given
        TabGroupName name1 = TabGroupName.create("탭 그룹 1");
        TabGroupName name2 = TabGroupName.create("탭 그룹 2");

        // when & then
        assertAll(
                () -> assertThat(name1).isNotEqualTo(name2),
                () -> assertThat(name1).doesNotHaveSameHashCodeAs(name2)
        );
    }
}
