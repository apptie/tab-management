package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.TabTree.TabNodeNotFoundException;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import com.management.tab.domain.user.vo.UserId;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabTreeTest {

    @Test
    void 빈_트리를_초기화할_수_있다() {
        // when
        TabTree actual = TabTree.create(1L);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTabGroupId().getValue()).isEqualTo(1L),
                () -> assertThat(actual.getRootTabNodes()).isEmpty(),
                () -> assertThat(actual.getTotalCount()).isZero()
        );
    }

    @Test
    void 루트_노드들로_트리를_초기화할_수_있다() {
        // given
        Tab rootTab1 = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭 1"),
                TabUrl.create("http://test1.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab rootTab2 = new Tab(
                TabId.create(2L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭 2"),
                TabUrl.create("http://test2.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode rootNode1 = TabNode.createRoot(rootTab1);
        TabNode rootNode2 = TabNode.createRoot(rootTab2);
        List<TabNode> rootNodes = List.of(rootNode1, rootNode2);

        // when
        TabTree actual = TabTree.create(1L, new ArrayList<>(rootNodes));

        // then
        assertAll(
                () -> assertThat(actual.getRootTabNodes()).hasSize(2),
                () -> assertThat(actual.getTotalCount()).isEqualTo(2)
        );
    }

    @Test
    void 그룹_ID가_null이면_트리를_초기화할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabTree.create(null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("탭 그룹 ID는 null일 수 없습니다.");
    }

    @Test
    void 루트_노드_리스트가_null이면_트리를_초기화할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabTree.create(1L, null))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("루트 노드들은 null일 수 없습니다.");
    }

    @Test
    void 노드가_다른_노드의_자손인지_확인할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        boolean actual = tabTree.isDescendant(TabId.create(1L), TabId.create(2L));

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void 깊은_자손_관계를_확인할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);

        Tab grandchildTab = new Tab(
                TabId.create(3L),
                TabId.create(2L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("손자 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode grandchildNode = TabNode.create(grandchildTab, 2);

        childNode.addChild(grandchildNode);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        boolean actual = tabTree.isDescendant(TabId.create(1L), TabId.create(3L));

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void 최대_깊이_미만이면_자식을_추가할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);
        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertDoesNotThrow(() -> tabTree.validateAddChildDepth(TabId.create(1L)));
    }

    @Test
    void 최대_깊이에_도달하면_자식을_추가할_수_없다() {
        // given
        Tab deepTab = new Tab(
                TabId.create(1L),
                TabId.create(99L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("깊은 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode deepNode = TabNode.create(deepTab, 9);
        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(deepNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateAddChildDepth(TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 존재하지_않는_부모에_자식을_추가할_수_없다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when & then
        assertThatThrownBy(() -> tabTree.validateAddChildDepth(TabId.create(999L)))
                .isInstanceOf(TabNodeNotFoundException.class)
                .hasMessage("지정한 탭 노드를 찾을 수 없습니다.");
    }

    @Test
    void 자기_자신을_부모로_이동할_수_없다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);
        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateMove(TabId.create(1L), TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자기 자신을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 순환_참조가_발생하면_이동할_수_없다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateMove(TabId.create(1L), TabId.create(2L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("순환 참조가 발생합니다: 자손을 부모로 설정할 수 없습니다.");
    }

    @Test
    void 이동_후_최대_깊이를_초과하면_이동할_수_없다() {
        // given
        Tab deepTab = new Tab(
                TabId.create(1L),
                TabId.create(99L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("깊은 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode deepNode = TabNode.create(deepTab, 9);
        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(deepNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateMoveDepth(TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 존재하지_않는_부모로는_이동할_수_없다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when & then
        assertThatThrownBy(() -> tabTree.validateMoveDepth(TabId.create(999L)))
                .isInstanceOf(TabNodeNotFoundException.class)
                .hasMessage("지정한 탭 노드를 찾을 수 없습니다.");
    }

    @Test
    void 서브트리와_함께_이동_시_최대_깊이를_초과하지_않으면_이동할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when & then
        assertDoesNotThrow(() -> tabTree.validateMoveDepthWithSubtree(TabId.create(2L), TabId.create(1L)));
    }

    @Test
    void 서브트리와_함께_이동_시_최대_깊이를_초과하면_이동할_수_없다() {
        // given
        Tab deepParentTab = new Tab(
                TabId.create(1L),
                TabId.create(99L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("깊은 부모 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode deepParentNode = TabNode.create(deepParentTab, 8);

        Tab movingTab = new Tab(
                TabId.create(2L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("이동할 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode movingNode = TabNode.createRoot(movingTab);

        Tab childOfMovingTab = new Tab(
                TabId.create(3L),
                TabId.create(2L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("이동할 탭의 자식"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childOfMovingNode = TabNode.create(childOfMovingTab, 1);
        movingNode.addChild(childOfMovingNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(deepParentNode, movingNode)));

        // when & then
        assertThatThrownBy(() -> tabTree.validateMoveDepthWithSubtree(TabId.create(2L), TabId.create(1L)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭의 계층은 10를 초과할 수 없습니다.");
    }

    @Test
    void 루트_노드의_형제를_찾을_수_있다() {
        // given
        Tab rootTab1 = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭 1"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab rootTab2 = new Tab(
                TabId.create(2L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭 2"),
                TabUrl.create("http://test.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode rootNode1 = TabNode.createRoot(rootTab1);
        TabNode rootNode2 = TabNode.createRoot(rootTab2);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode1, rootNode2)));

        // when
        List<TabNode> actual = tabTree.findSiblings(TabId.create(1L));

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 자식_노드의_형제를_찾을_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab1 = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭 1"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab childTab2 = new Tab(
                TabId.create(3L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭 2"),
                TabUrl.create("http://test.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode childNode1 = TabNode.create(childTab1, 1);
        TabNode childNode2 = TabNode.create(childTab2, 1);
        rootNode.addChild(childNode1);
        rootNode.addChild(childNode2);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        List<TabNode> actual = tabTree.findSiblings(TabId.create(2L));

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 존재하지_않는_노드의_형제를_찾으면_빈_리스트를_반환한다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when
        List<TabNode> actual = tabTree.findSiblings(TabId.create(999L));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 트리의_모든_탭을_조회할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        List<Tab> actual = tabTree.getAllTabs();

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 트리의_전체_노드_개수를_조회할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        int actual = tabTree.getTotalCount();

        // then
        assertThat(actual).isEqualTo(2);
    }

    @Test
    void 트리의_최대_깊이를_조회할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        int actual = tabTree.getMaxDepth();

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void 빈_트리에서_다음_루트_위치는_기본값이다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when
        TabPosition actual = tabTree.getNextRootPosition();

        // then
        assertThat(actual).isEqualTo(TabPosition.defaultPosition());
    }

    @Test
    void 루트_노드가_있을_때_다음_루트_위치를_조회할_수_있다() {
        // given
        Tab rootTab1 = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭 1"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab rootTab2 = new Tab(
                TabId.create(2L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭 2"),
                TabUrl.create("http://test.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode rootNode1 = TabNode.createRoot(rootTab1);
        TabNode rootNode2 = TabNode.createRoot(rootTab2);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode1, rootNode2)));

        // when
        TabPosition actual = tabTree.getNextRootPosition();

        // then
        assertThat(actual.getValue()).isEqualTo(2);
    }

    @Test
    void 자식_노드가_없을_때_다음_자식_위치는_기본값이다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);
        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        TabPosition actual = tabTree.getNextChildPosition(TabId.create(1L));

        // then
        assertThat(actual).isEqualTo(TabPosition.defaultPosition());
    }

    @Test
    void 자식_노드가_있을_때_다음_자식_위치를_조회할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab1 = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭 1"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab childTab2 = new Tab(
                TabId.create(3L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭 2"),
                TabUrl.create("http://test.com"),
                TabPosition.create(1),
                AuditTimestamps.now()
        );
        TabNode childNode1 = TabNode.create(childTab1, 1);
        TabNode childNode2 = TabNode.create(childTab2, 1);
        rootNode.addChild(childNode1);
        rootNode.addChild(childNode2);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        TabPosition actual = tabTree.getNextChildPosition(TabId.create(1L));

        // then
        assertThat(actual.getValue()).isEqualTo(2);
    }

    @Test
    void 존재하지_않는_부모의_다음_자식_위치를_조회하면_예외가_발생한다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when & then
        assertThatThrownBy(() -> tabTree.getNextChildPosition(TabId.create(999L)))
                .isInstanceOf(TabNodeNotFoundException.class)
                .hasMessage("지정한 탭 노드를 찾을 수 없습니다.");
    }

    @Test
    void 루트_노드의_깊이를_조회할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);
        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        int actual = tabTree.findDepth(TabId.create(1L));

        // then
        assertThat(actual).isZero();
    }

    @Test
    void 자식_노드의_깊이를_조회할_수_있다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("루트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("자식 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        rootNode.addChild(childNode);

        TabTree tabTree = TabTree.create(1L, new ArrayList<>(List.of(rootNode)));

        // when
        int actual = tabTree.findDepth(TabId.create(2L));

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void 존재하지_않는_노드의_깊이를_조회하면_예외가_발생한다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when & then
        assertThatThrownBy(() -> tabTree.findDepth(TabId.create(999L)))
                .isInstanceOf(TabTree.TabNodeNotFoundException.class)
                .hasMessage("지정한 탭 노드를 찾을 수 없습니다.");
    }

    @Test
    void 깊이_합이_최대_깊이_미만이면_탭_생성이_가능하다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when & then
        assertDoesNotThrow(() -> tabTree.validateCreateDepth(5, 3));
    }

    @Test
    void 깊이_합이_최대_깊이를_초과하면_예외가_발생한다() {
        // given
        TabTree tabTree = TabTree.create(1L);

        // when & then
        assertThatThrownBy(() -> tabTree.validateCreateDepth(7, 4))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("요청의 최대 깊이(7)와 현재 깊이(4)를 합하면 최대 허용 깊이(10)를 초과합니다.");
    }
}
