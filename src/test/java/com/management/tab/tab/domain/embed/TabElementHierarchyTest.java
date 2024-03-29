package com.management.tab.tab.domain.embed;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.management.tab.tab.domain.exception.InvalidTabElementDepthException;
import com.management.tab.tab.domain.exception.InvalidTabElementOrderException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabElementHierarchyTest {

    @Test
    void 생성자는_유효한_파라미터를_전달하면_TabElementHierarchy를_생성한다() {
        // when & then
        TabElementHierarchy tabElementHierarchy = assertDoesNotThrow(() -> new TabElementHierarchy(1, 1));

        assertThat(tabElementHierarchy).isInstanceOf(TabElementHierarchy.class);
    }

    @Test
    void 생성자는_유효하지_않은_depth_값을_전달하면_예외가_발생한다() {
        // given
        TabElementHierarchy tabElementHierarchy = new TabElementHierarchy(0, 0);

        // when & then
        int invalidDepth = -1;

        assertThatThrownBy(() -> tabElementHierarchy.changeHierarchy(1, invalidDepth))
                .isInstanceOf(InvalidTabElementDepthException.class);
    }

    @Test
    void 생성자는_유효하지_않은_order_값을_전달하면_예외가_발생한다() {
        // given
        TabElementHierarchy tabElementHierarchy = new TabElementHierarchy(0, 0);

        // when & then
        int invalidOrder = -1;

        assertThatThrownBy(() -> tabElementHierarchy.changeHierarchy(invalidOrder, 1))
                .isInstanceOf(InvalidTabElementOrderException.class);
    }

    @Test
    void changeHierarchy_메서드는_유효한_파라미터를_전달하면_변경한_값으로_초기화한_TabElementHierarchy를_반환한다() {
        // given
        TabElementHierarchy tabElementHierarchy = new TabElementHierarchy(0, 0);

        // when
        int expectedOrder = 1;
        int expectedDepth = 1;

        TabElementHierarchy actual = tabElementHierarchy.changeHierarchy(expectedOrder, expectedDepth);

        // then
        assertThat(actual.getOrder()).isEqualTo(expectedOrder);
        assertThat(actual.getDepth()).isEqualTo(expectedDepth);
    }

    @Test
    void changeHierarchy_메서드는_유효하지_않은_depth_값을_전달하면_예외가_발생한다() {
        // given
        TabElementHierarchy tabElementHierarchy = new TabElementHierarchy(0, 0);

        // when & then
        int invalidDepth = -1;

        assertThatThrownBy(() -> tabElementHierarchy.changeHierarchy(1, invalidDepth))
                .isInstanceOf(InvalidTabElementDepthException.class);
    }

    @Test
    void changeHierarchy_메서드는_유효하지_않은_order_값을_전달하면_예외가_발생한다() {
        // given
        TabElementHierarchy tabElementHierarchy = new TabElementHierarchy(0, 0);

        // when & then
        int invalidOrder = -1;

        assertThatThrownBy(() -> tabElementHierarchy.changeHierarchy(invalidOrder, invalidOrder))
                .isInstanceOf(InvalidTabElementOrderException.class);
    }
}
