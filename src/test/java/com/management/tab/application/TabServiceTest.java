package com.management.tab.application;

import com.management.tab.domain.tab.TabTree;
import com.management.tab.domain.tab.vo.TabId;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/service/tab-service-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TabServiceTest {

    @Autowired
    TabService tabService;

    @Test
    void 루트_탭을_생성할_수_있다() {
        // when
        TabId actual = tabService.createRootTab(1L, "새 루트 탭", "https://new-root.com");

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 루트_탭_생성_시_마지막_위치_다음에_추가된다() {
        // given
        TabTree beforeTree = tabService.getTabTree(1L);
        int beforeCount = beforeTree.getTotalCount();

        // when
        tabService.createRootTab(1L, "새 루트 탭", "https://new-root.com");

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(actual.getTotalCount()).isEqualTo(beforeCount + 1);
    }

    @Test
    void 자식_탭을_생성할_수_있다() {
        // when
        TabId actual = tabService.createChildTab(100L, "새 자식 탭", "https://new-child.com");

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 자식_탭_생성_시_부모의_마지막_위치_다음에_추가된다() {
        // given
        TabTree beforeTree = tabService.getTabTree(1L);
        int beforeCount = beforeTree.getTotalCount();

        // when
        tabService.createChildTab(100L, "새 자식 탭", "https://new-child.com");

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(actual.getTotalCount()).isEqualTo(beforeCount + 1);
    }

    @Test
    void 최대_깊이를_초과하면_자식_탭을_생성할_수_없다() {
        // when & then
        assertThatThrownBy(() -> tabService.createChildTab(112L, "깊은 자식 탭", "https://deep-child.com"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 탭을_삭제할_수_있다() {
        // when
        tabService.deleteTab(105L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(
                actual.getAllTabs()
                      .stream()
                      .filter(tab -> tab.isEqualId(TabId.create(105L)))
                      .findFirst()
        ).isEmpty();
    }

    @Test
    void 탭을_서브트리와_함께_삭제할_수_있다() {
        // given
        TabTree beforeTree = tabService.getTabTree(1L);
        int beforeCount = beforeTree.getTotalCount();

        // when
        tabService.deleteTabWithSubtree(101L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(actual.getTotalCount()).isLessThan(beforeCount);
    }

    @Test
    void 서브트리와_함께_삭제하면_자손도_모두_삭제된다() {
        // when
        tabService.deleteTabWithSubtree(101L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertAll(
                () -> assertThat(actual.getAllTabs()
                                       .stream()
                                       .filter(tab -> tab.isEqualId(TabId.create(101L)))
                                       .findFirst()).isEmpty(),
                () -> assertThat(actual.getAllTabs()
                                       .stream()
                                       .filter(tab -> tab.isEqualId(TabId.create(103L)))
                                       .findFirst()).isEmpty(),
                () -> assertThat(actual.getAllTabs()
                                       .stream()
                                       .filter(tab -> tab.isEqualId(TabId.create(104L)))
                                       .findFirst()).isEmpty()
        );
    }

    @Test
    void 탭을_루트로_이동할_수_있다() {
        // when
        tabService.moveRoot(101L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(
                actual.getAllTabs()
                      .stream()
                      .filter(tab -> tab.isEqualId(TabId.create(101L)))
                      .findFirst()
        ).isPresent()
         .hasValueSatisfying(tab -> assertThat(tab.parentId()).isSameAs(TabId.EMPTY_TAB_ID));
    }

    @Test
    void 탭을_서브트리와_함께_루트로_이동할_수_있다() {
        // when
        tabService.moveRootWithSubtree(101L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(
                actual.getAllTabs()
                      .stream()
                      .filter(tab -> tab.isEqualId(TabId.create(101L)))
                      .findFirst()
        ).isPresent()
         .hasValueSatisfying(tab -> assertThat(tab.parentId()).isSameAs(TabId.EMPTY_TAB_ID));
    }

    @Test
    void 탭을_다른_부모로_이동할_수_있다() {
        // when
        tabService.move(103L, 102L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(
                actual.getAllTabs()
                      .stream()
                      .filter(tab -> tab.isEqualId(TabId.create(103L)))
                      .findFirst()
        ).isPresent()
         .hasValueSatisfying(tab -> assertThat(tab.getParentId()).isEqualTo(102L));
    }

    @Test
    void 자기_자신을_부모로_이동할_수_없다() {
        // when & then
        assertThatThrownBy(() -> tabService.move(101L, 101L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자기 자신을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 자손을_부모로_이동할_수_없다() {
        // when & then
        assertThatThrownBy(() -> tabService.move(101L, 103L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("순환 참조가 발생합니다: 자손을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 최대_깊이를_초과하면_이동할_수_없다() {
        // when & then
        assertThatThrownBy(() -> tabService.move(101L, 112L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 탭을_서브트리와_함께_이동할_수_있다() {
        // when
        tabService.moveWithSubtree(101L, 102L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(actual.getAllTabs().stream()
                         .filter(tab -> tab.isEqualId(TabId.create(101L)))
                         .findFirst())
                .isPresent()
                .hasValueSatisfying(tab -> assertThat(tab.getParentId()).isEqualTo(102L));
    }

    @Test
    void 서브트리와_함께_이동하면_자손도_함께_이동한다() {
        // when
        tabService.moveWithSubtree(101L, 102L);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertAll(
                () -> assertThat(actual.getAllTabs()
                                       .stream()
                                       .filter(tab -> tab.isEqualId(TabId.create(103L)))
                                       .findFirst()).isPresent(),
                () -> assertThat(actual.getAllTabs()
                                       .stream()
                                       .filter(tab -> tab.isEqualId(TabId.create(104L)))
                                       .findFirst()).isPresent()
        );
    }

    @Test
    void 서브트리와_함께_이동_시_최대_깊이를_초과하면_이동할_수_없다() {
        // when & then
        assertThatThrownBy(() -> tabService.moveWithSubtree(101L, 111L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 같은_레벨이_아닌_탭의_순서를_변경할_수_없다() {
        // when & then
        assertThatThrownBy(() -> tabService.reorderTab(101L, 103L, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 레벨의 탭만 순서를 변경할 수 있습니다");
    }

    @Test
    void 탭의_정보를_수정할_수_있다() {
        // when
        tabService.updateTab(100L, "수정된 제목", "https://updated.com");

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(actual.getAllTabs()
                         .stream()
                         .filter(tab -> tab.isEqualId(TabId.create(100L)))
                         .findFirst()
        ).isPresent()
         .hasValueSatisfying(
                 tab -> assertAll(
                         () -> assertThat(tab.getTitle()).isEqualTo("수정된 제목"),
                         () -> assertThat(tab.getUrl()).isEqualTo("https://updated.com")
                 )
         );
    }

    @Test
    void 그룹의_탭_트리를_조회할_수_있다() {
        // when
        TabTree actual = tabService.getTabTree(1L);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTotalCount()).isGreaterThan(0)
        );
    }

    @Test
    void 루트_레벨_탭의_순서를_변경할_수_있다() {
        // when & then
        assertDoesNotThrow(() -> tabService.reorderTab(100L, 200L, true));
    }

    @Test
    void 루트_탭과_일반_탭의_순서를_변경할_수_없다() {
        // when & then
        assertThatThrownBy(() -> tabService.reorderTab(100L, 101L, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 레벨의 탭만 순서를 변경할 수 있습니다");
    }

    @Test
    void 루트_레벨_탭_순서_변경_시_position이_업데이트된다() {
        // when
        tabService.reorderTab(100L, 200L, false);

        // then
        TabTree actual = tabService.getTabTree(1L);

        assertThat(
                actual.getAllTabs()
                      .stream()
                      .filter(tab -> tab.isEqualId(TabId.create(100L)))
                      .findFirst()
        ).isPresent()
         .hasValueSatisfying(tab -> assertThat(tab.getPosition()).isZero());
    }
}
