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
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/insert-tab-service-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TabServiceTest {

    @Autowired
    TabService tabService;

    @Test
    void 루트_탭을_생성할_수_있다() {
        // given
        Long groupId = 1L;
        String title = "새 루트 탭";
        String url = "https://new-root.com";

        // when
        TabId actual = tabService.createRootTab(groupId, title, url);

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 루트_탭_생성_시_마지막_위치_다음에_추가된다() {
        // given
        Long groupId = 1L;
        String title = "새 루트 탭";
        String url = "https://new-root.com";
        TabTree beforeTree = tabService.getTabTree(groupId);
        int beforeCount = beforeTree.getTotalCount();

        // when
        tabService.createRootTab(groupId, title, url);

        // then
        TabTree actual = tabService.getTabTree(groupId);
        assertThat(actual.getTotalCount()).isEqualTo(beforeCount + 1);
    }

    @Test
    void 자식_탭을_생성할_수_있다() {
        // given
        Long parentId = 100L;
        String title = "새 자식 탭";
        String url = "https://new-child.com";

        // when
        TabId actual = tabService.createChildTab(parentId, title, url);

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 자식_탭_생성_시_부모의_마지막_위치_다음에_추가된다() {
        // given
        Long parentId = 100L;
        String title = "새 자식 탭";
        String url = "https://new-child.com";
        TabTree beforeTree = tabService.getTabTree(1L);
        int beforeCount = beforeTree.getTotalCount();

        // when
        tabService.createChildTab(parentId, title, url);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.getTotalCount()).isEqualTo(beforeCount + 1);
    }

    @Test
    void 최대_깊이를_초과하면_자식_탭을_생성할_수_없다() {
        // given
        Long deepParentId = 112L;  // depth 9 탭 (MAX_DEPTH - 1)
        String title = "깊은 자식 탭";
        String url = "https://deep-child.com";

        // when & then
        assertThatThrownBy(() -> tabService.createChildTab(deepParentId, title, url))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void 탭을_삭제할_수_있다() {
        // given
        Long tabId = 105L;

        // when
        tabService.deleteTab(tabId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.findNode(TabId.create(tabId))).isEmpty();
    }

    @Test
    void 탭을_서브트리와_함께_삭제할_수_있다() {
        // given
        Long tabId = 101L;
        TabTree beforeTree = tabService.getTabTree(1L);
        int beforeCount = beforeTree.getTotalCount();

        // when
        tabService.deleteTabWithSubtree(tabId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.getTotalCount()).isLessThan(beforeCount);
    }

    @Test
    void 서브트리와_함께_삭제하면_자손도_모두_삭제된다() {
        // given
        Long tabId = 101L;

        // when
        tabService.deleteTabWithSubtree(tabId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertAll(
                () -> assertThat(actual.findNode(TabId.create(101L))).isEmpty(),
                () -> assertThat(actual.findNode(TabId.create(103L))).isEmpty(),
                () -> assertThat(actual.findNode(TabId.create(104L))).isEmpty()
        );
    }

    @Test
    void 탭을_루트로_이동할_수_있다() {
        // given
        Long tabId = 101L;

        // when
        tabService.moveRoot(tabId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.findNode(TabId.create(tabId)))
                .isPresent()
                .hasValueSatisfying(node -> assertThat(node.isRoot()).isTrue());
    }

    @Test
    void 탭을_서브트리와_함께_루트로_이동할_수_있다() {
        // given
        Long tabId = 101L;

        // when
        tabService.moveRootWithSubtree(tabId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.findNode(TabId.create(tabId)))
                .isPresent()
                .hasValueSatisfying(node -> assertThat(node.isRoot()).isTrue());
    }

    @Test
    void 탭을_다른_부모로_이동할_수_있다() {
        // given
        Long tabId = 103L;
        Long newParentId = 102L;

        // when
        tabService.move(tabId, newParentId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.findNode(TabId.create(tabId)))
                .isPresent()
                .hasValueSatisfying(node -> assertThat(node.getParentId()).isEqualTo(TabId.create(newParentId)));
    }

    @Test
    void 자기_자신을_부모로_이동할_수_없다() {
        // given
        Long tabId = 101L;
        Long newParentId = 101L;

        // when & then
        assertThatThrownBy(() -> tabService.move(tabId, newParentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자기 자신을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 자손을_부모로_이동할_수_없다() {
        // given
        Long tabId = 101L;
        Long newParentId = 103L;

        // when & then
        assertThatThrownBy(() -> tabService.move(tabId, newParentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("순환 참조가 발생합니다: 자손을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 최대_깊이를_초과하면_이동할_수_없다() {
        // given
        Long tabId = 101L;
        Long deepParentId = 112L;

        // when & then
        assertThatThrownBy(() -> tabService.move(tabId, deepParentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 탭을_서브트리와_함께_이동할_수_있다() {
        // given
        Long tabId = 101L;
        Long newParentId = 102L;

        // when
        tabService.moveWithSubtree(tabId, newParentId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.findNode(TabId.create(tabId)))
                .isPresent()
                .hasValueSatisfying(node -> assertThat(node.getParentId()).isEqualTo(TabId.create(newParentId)));
    }

    @Test
    void 서브트리와_함께_이동하면_자손도_함께_이동한다() {
        // given
        Long tabId = 101L;
        Long newParentId = 102L;

        // when
        tabService.moveWithSubtree(tabId, newParentId);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertAll(
                () -> assertThat(actual.findNode(TabId.create(103L))).isPresent(),
                () -> assertThat(actual.findNode(TabId.create(104L))).isPresent()
        );
    }

    @Test
    void 서브트리와_함께_이동_시_최대_깊이를_초과하면_이동할_수_없다() {
        // given
        Long tabId = 101L;
        Long deepParentId = 111L;

        // when & then
        assertThatThrownBy(() -> tabService.moveWithSubtree(tabId, deepParentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 같은_레벨이_아닌_탭의_순서를_변경할_수_없다() {
        // given
        Long tabId = 101L;
        Long targetTabId = 103L;

        // when & then
        assertThatThrownBy(() -> tabService.reorderTab(tabId, targetTabId, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 레벨의 탭만 순서를 변경할 수 있습니다");
    }

    @Test
    void 탭의_정보를_수정할_수_있다() {
        // given
        Long tabId = 100L;
        String newTitle = "수정된 제목";
        String newUrl = "https://updated.com";

        // when
        tabService.updateTab(tabId, newTitle, newUrl);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.findNode(TabId.create(tabId)))
                .isPresent()
                .hasValueSatisfying(node -> assertAll(
                        () -> assertThat(node.getTab().getTitle().getValue()).isEqualTo(newTitle),
                        () -> assertThat(node.getTab().getUrl().getValue()).isEqualTo(newUrl)
                ));
    }

    @Test
    void 그룹의_탭_트리를_조회할_수_있다() {
        // given
        Long groupId = 1L;

        // when
        TabTree actual = tabService.getTabTree(groupId);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTotalCount()).isGreaterThan(0)
        );
    }

    @Test
    void 루트_레벨_탭의_순서를_변경할_수_있다() {
        // given
        Long tabId = 100L;
        Long targetTabId = 200L;

        // when & then
        assertDoesNotThrow(() -> tabService.reorderTab(tabId, targetTabId, true));
    }

    @Test
    void 루트_탭과_일반_탭의_순서를_변경할_수_없다() {
        // given
        Long rootTabId = 100L;
        Long childTabId = 101L;

        // when & then
        assertThatThrownBy(() -> tabService.reorderTab(rootTabId, childTabId, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("같은 레벨의 탭만 순서를 변경할 수 있습니다");
    }

    @Test
    void 루트_레벨_탭_순서_변경_시_position이_업데이트된다() {
        // given
        Long tabId = 100L;
        Long targetTabId = 200L;

        // when
        tabService.reorderTab(tabId, targetTabId, false);

        // then
        TabTree actual = tabService.getTabTree(1L);
        assertThat(actual.findNode(TabId.create(tabId)))
                .isPresent()
                .hasValueSatisfying(node -> assertThat(node.getTab().getPosition().getValue()).isZero());
    }
}
