package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabTreeTest {

    @Test
    void 빈_트리를_초기화할_수_있다() {
        // given
        Long groupId = 1L;

        // when
        TabTree tabTree = TabTree.create(groupId);

        // then
        assertAll(
                () -> assertThat(tabTree).isNotNull(),
                () -> assertThat(tabTree.getTabGroupId().getValue()).isEqualTo(groupId),
                () -> assertThat(tabTree.getRootTabNodes()).isEmpty(),
                () -> assertThat(tabTree.getTotalCount()).isZero()
        );
    }

    @Test
    void 루트_노드들로_트리를_초기화할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab1 = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭 1"),
                TabUrl.create("http://test1.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab rootTab2 = new Tab(
                TabId.create(2L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭 2"),
                TabUrl.create("http://test2.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode rootNode1 = TabNode.createRoot(rootTab1);
        TabNode rootNode2 = TabNode.createRoot(rootTab2);
        List<TabNode> rootNodes = List.of(rootNode1, rootNode2);

        // when
        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(rootNodes));

        // then
        assertAll(
                () -> assertThat(tabTree.getRootTabNodes()).hasSize(2),
                () -> assertThat(tabTree.getTotalCount()).isEqualTo(2)
        );
    }

    @Test
    void 그룹_ID가_null이면_트리를_초기화할_수_없다() {
        // given
        Long groupId = null;

        // when & then
        assertThatThrownBy(() -> TabTree.create(groupId))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("탭 그룹 ID는 null일 수 없습니다.");
    }

    @Test
    void 루트_노드_리스트가_null이면_트리를_초기화할_수_없다() {
        // given
        Long groupId = 1L;
        List<TabNode> rootNodes = null;

        // when & then
        assertThatThrownBy(() -> TabTree.create(groupId, rootNodes))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("루트 노드들은 null일 수 없습니다.");
    }

    @Test
    void 노드가_다른_노드의_자손인지_확인할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1, TabId.create(1L));
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when
        boolean isDescendant = tabTree.isDescendant(TabId.create(1L), TabId.create(2L));

        // then
        assertThat(isDescendant).isTrue();
    }

    @Test
    void 깊은_자손_관계를_확인할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1, TabId.create(1L));

        Tab grandchildTab = new Tab(
                TabId.create(3L),
                TabId.create(2L),
                TabGroupId.create(groupId),
                TabTitle.create("손자 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode grandchildNode = TabNode.create(grandchildTab, 2, TabId.create(2L));

        childNode.addChild(grandchildNode);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when
        boolean isDescendant = tabTree.isDescendant(TabId.create(1L), TabId.create(3L));

        // then
        assertThat(isDescendant).isTrue();
    }

    @Test
    void 최대_깊이_미만이면_자식을_추가할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);
        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertDoesNotThrow(() -> tabTree.validateAddChild(TabId.create(1L)));
    }

    @Test
    void 최대_깊이에_도달하면_자식을_추가할_수_없다() {
        // given
        Long groupId = 1L;
        Tab deepTab = new Tab(
                TabId.create(1L),
                TabId.create(99L),
                TabGroupId.create(groupId),
                TabTitle.create("깊은 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode deepNode = TabNode.create(deepTab, 9, TabId.create(99L));
        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(deepNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateAddChild(TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 존재하지_않는_부모에_자식을_추가할_수_없다() {
        // given
        Long groupId = 1L;
        TabTree tabTree = TabTree.create(groupId);

        // when & then
        assertThatThrownBy(() -> tabTree.validateAddChild(TabId.create(999L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("부모 탭을 찾을 수 없습니다.");
    }

    @Test
    void 자기_자신을_부모로_이동할_수_없다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);
        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateMove(TabId.create(1L), TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자기 자신을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 순환_참조가_발생하면_이동할_수_없다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1, TabId.create(1L));
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateMove(TabId.create(1L), TabId.create(2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("순환 참조가 발생합니다: 자손을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 이동_후_최대_깊이를_초과하면_이동할_수_없다() {
        // given
        Long groupId = 1L;
        Tab deepTab = new Tab(
                TabId.create(1L),
                TabId.create(99L),
                TabGroupId.create(groupId),
                TabTitle.create("깊은 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode deepNode = TabNode.create(deepTab, 9, TabId.create(99L));
        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(deepNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateMoveDepth(TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 존재하지_않는_부모로는_이동할_수_없다() {
        // given
        Long groupId = 1L;
        TabTree tabTree = TabTree.create(groupId);

        // when & then
        assertThatThrownBy(() -> tabTree.validateMoveDepth(TabId.create(999L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("새 부모를 찾을 수 없습니다.");
    }

    @Test
    void 서브트리와_함께_이동_시_최대_깊이를_초과하지_않으면_이동할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1, TabId.create(1L));
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertDoesNotThrow(() -> tabTree.validateMoveDepthWithSubtree(TabId.create(2L), TabId.create(1L)));
    }

    @Test
    void 서브트리와_함께_이동_시_최대_깊이를_초과하면_이동할_수_없다() {
        // given
        Long groupId = 1L;

        // depth 8의 부모 노드 생성
        Tab deepParentTab = new Tab(
                TabId.create(1L),
                TabId.create(99L),
                TabGroupId.create(groupId),
                TabTitle.create("깊은 부모 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode deepParentNode = TabNode.create(deepParentTab, 8, TabId.create(99L));

        // depth 0의 이동할 노드 생성 (서브트리 depth 1 포함)
        Tab movingTab = new Tab(
                TabId.create(2L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("이동할 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode movingNode = TabNode.createRoot(movingTab);

        Tab childOfMovingTab = new Tab(
                TabId.create(3L),
                TabId.create(2L),
                TabGroupId.create(groupId),
                TabTitle.create("이동할 탭의 자식"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childOfMovingNode = TabNode.create(childOfMovingTab, 1, TabId.create(2L));
        movingNode.addChild(childOfMovingNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(deepParentNode, movingNode)));

        // when & then
        // 계산: 8(새부모) + 1(이동노드) + 1(서브트리깊이) = 10 (MAX_DEPTH와 같음, 초과)
        assertThatThrownBy(() -> tabTree.validateMoveDepthWithSubtree(TabId.create(2L), TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 특정_ID로_노드를_찾을_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);
        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when
        Optional<TabNode> foundNode = tabTree.findNode(TabId.create(1L));

        // then
        assertAll(
                () -> assertThat(foundNode).isPresent(),
                () -> assertThat(foundNode.get().getId()).isEqualTo(TabId.create(1L))
        );
    }

    @Test
    void 존재하지_않는_노드를_찾으면_빈_Optional을_반환한다() {
        // given
        Long groupId = 1L;
        TabTree tabTree = TabTree.create(groupId);

        // when
        Optional<TabNode> foundNode = tabTree.findNode(TabId.create(999L));

        // then
        assertThat(foundNode).isEmpty();
    }

    @Test
    void 루트_노드의_형제를_찾을_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab1 = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭 1"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab rootTab2 = new Tab(
                TabId.create(2L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭 2"),
                TabUrl.create("http://test.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode rootNode1 = TabNode.createRoot(rootTab1);
        TabNode rootNode2 = TabNode.createRoot(rootTab2);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode1, rootNode2)));

        // when
        List<TabNode> siblings = tabTree.findSiblings(TabId.create(1L));

        // then
        assertThat(siblings).hasSize(2);
    }

    @Test
    void 자식_노드의_형제를_찾을_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab1 = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭 1"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab childTab2 = new Tab(
                TabId.create(3L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭 2"),
                TabUrl.create("http://test.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode childNode1 = TabNode.create(childTab1, 1, TabId.create(1L));
        TabNode childNode2 = TabNode.create(childTab2, 1, TabId.create(1L));
        rootNode.addChild(childNode1);
        rootNode.addChild(childNode2);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when
        List<TabNode> siblings = tabTree.findSiblings(TabId.create(2L));

        // then
        assertThat(siblings).hasSize(2);
    }

    @Test
    void 존재하지_않는_노드의_형제를_찾으면_빈_리스트를_반환한다() {
        // given
        Long groupId = 1L;
        TabTree tabTree = TabTree.create(groupId);

        // when
        List<TabNode> siblings = tabTree.findSiblings(TabId.create(999L));

        // then
        assertThat(siblings).isEmpty();
    }

    @Test
    void 트리의_모든_탭을_조회할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1, TabId.create(1L));
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when
        List<Tab> allTabs = tabTree.getAllTabs();

        // then
        assertThat(allTabs).hasSize(2);
    }

    @Test
    void 트리의_전체_노드_개수를_조회할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1, TabId.create(1L));
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when
        int totalCount = tabTree.getTotalCount();

        // then
        assertThat(totalCount).isEqualTo(2);
    }

    @Test
    void 트리의_최대_깊이를_조회할_수_있다() {
        // given
        Long groupId = 1L;
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                TabGroupId.create(groupId),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                TabGroupId.create(groupId),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1, TabId.create(1L));
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(groupId, new ArrayList<>(List.of(rootNode)));

        // when
        int maxDepth = tabTree.getMaxDepth();

        // then
        assertThat(maxDepth).isEqualTo(1);
    }
}
