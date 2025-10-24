package com.management.tab.domain.tab.vo;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabPositionTest {

    @Test
    void 유효한_0_이상의_값으로_TabPosition을_초기화할_수_있다() {
        // when
        TabPosition position = TabPosition.create(0);

        // then
        assertAll(
                () -> assertThat(position).isNotNull(),
                () -> assertThat(position.getValue()).isZero()
        );
    }

    @Test
    void 음수로는_TabPosition을_초기화할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabPosition.create(-1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("위치는 0 이상이어야 합니다");
    }

    @Test
    void defaultPosition은_position으로_0을_반환한다() {
        // when
        TabPosition defaultPosition = TabPosition.defaultPosition();

        // then
        assertAll(
                () -> assertThat(defaultPosition).isNotNull(),
                () -> assertThat(defaultPosition.getValue()).isZero()
        );
    }

    @Test
    void 해당_TabPosition이_첫_번째_위치인지_확인한다() {
        // when
        TabPosition defaultPosition = TabPosition.defaultPosition();

        // then
        assertThat(defaultPosition.isFirst()).isTrue();
    }

    @Test
    void next는_현재_위치보다_1_증가한_TabPosition을_반환한다() {
        // given
        TabPosition position = TabPosition.create(5);

        // when
        TabPosition nextPosition = position.next();

        // then
        assertThat(nextPosition.getValue()).isEqualTo(6);
    }

    @Test
    void next는_새로운_TabPosition_인스턴스를_반환한다() {
        // given
        TabPosition position = TabPosition.create(3);

        // when
        TabPosition nextPosition = position.next();

        // then
        assertAll(
                () -> assertThat(nextPosition).isNotSameAs(position),
                () -> assertThat(position.getValue()).isEqualTo(3),
                () -> assertThat(nextPosition.getValue()).isEqualTo(4)
        );
    }

    @Test
    void 같은_값을_가진_TabPosition은_동등하다() {
        // given
        TabPosition position1 = TabPosition.create(5);
        TabPosition position2 = TabPosition.create(5);

        // when & then
        assertAll(
                () -> assertThat(position1).isEqualTo(position2),
                () -> assertThat(position1).hasSameHashCodeAs(position2)
        );
    }

    @Test
    void 다른_값을_가진_TabPosition은_동등하지_않다() {
        // given
        TabPosition position1 = TabPosition.create(5);
        TabPosition position2 = TabPosition.create(10);

        // when & then
        assertAll(
                () -> assertThat(position1).isNotEqualTo(position2),
                () -> assertThat(position1).doesNotHaveSameHashCodeAs(position2)
        );
    }
}
