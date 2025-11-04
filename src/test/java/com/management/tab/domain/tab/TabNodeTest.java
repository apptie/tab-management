package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import com.management.tab.domain.user.vo.UserId;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabNodeTest {

    @Test
    void TabNode를_초기화할_수_있다() {
        // given
        Tab tab = new Tab(
                TabId.create(1L),
                TabId.create(10L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Integer depth = 2;
        TabId parentId = TabId.create(10L);

        // when
        TabNode tabNode = TabNode.create(tab, depth);

        // then
        assertAll(
                () -> assertThat(tabNode).isNotNull(),
                () -> assertThat(tabNode.getTab()).isEqualTo(tab),
                () -> assertThat(tabNode.getDepth()).isEqualTo(depth),
                () -> assertThat(tabNode.parentId()).isEqualTo(parentId),
                () -> assertThat(tabNode.getChildren()).isEmpty()
        );
    }

    @Test
    void 루트_TabNode를_초기화할_수_있다() {
        // given
        Tab tab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );

        // when
        TabNode rootNode = TabNode.createRoot(tab);

        // then
        assertAll(
                () -> assertThat(rootNode).isNotNull(),
                () -> assertThat(rootNode.getTab()).isEqualTo(tab),
                () -> assertThat(rootNode.getDepth()).isZero(),
                () -> assertThat(rootNode.parentId()).isSameAs(TabId.EMPTY_TAB_ID),
                () -> assertThat(rootNode.getChildren()).isEmpty()
        );
    }

    @Test
    void 자식_노드를_추가할_수_있다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode parentNode = TabNode.createRoot(parentTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);

        // when
        parentNode.addChild(childNode);

        // then
        assertAll(
                () -> assertThat(parentNode.getChildren()).hasSize(1),
                () -> assertThat(parentNode.getChildren()).contains(childNode),
                () -> assertThat(parentNode.hasChildren()).isTrue()
        );
    }

    @Test
    void null인_자식_노드는_추가할_수_없다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode parentNode = TabNode.createRoot(parentTab);

        // when & then
        assertThatThrownBy(() -> parentNode.addChild(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자식 노드는 null일 수 없습니다.");
    }

    @Test
    void 자기_자신을_자식으로_추가할_수_없다() {
        // given
        Tab tab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode node = TabNode.createRoot(tab);

        // when & then
        assertThatThrownBy(() -> node.addChild(node))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("자기 자신을 자식으로 추가할 수 없습니다.");
    }

    @Test
    void 자식_노드를_제거할_수_있다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode parentNode = TabNode.createRoot(parentTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        parentNode.addChild(childNode);

        // when
        parentNode.removeChild(TabId.create(2L));

        // then
        assertAll(
                () -> assertThat(parentNode.getChildren()).isEmpty(),
                () -> assertThat(parentNode.hasChildren()).isFalse()
        );
    }

    @Test
    void 존재하지_않는_자식_노드를_제거해도_예외가_발생하지_않는다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode parentNode = TabNode.createRoot(parentTab);

        // when & then
        parentNode.removeChild(TabId.create(999L));
        assertThat(parentNode.getChildren()).isEmpty();
    }

    @Test
    void 자식_조회_시_불변_리스트를_반환한다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode parentNode = TabNode.createRoot(parentTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        parentNode.addChild(childNode);

        // when
        var children = parentNode.getChildren();

        // then
        assertThatThrownBy(() -> children.add(childNode))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void 자식이_있는지_여부를_반환한다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode parentNode = TabNode.createRoot(parentTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        parentNode.addChild(childNode);

        // when
        boolean hasChildren = parentNode.hasChildren();

        // then
        assertThat(hasChildren).isTrue();
    }

    @Test
    void 현재_TabNode의_Tab이_루트인지_확인한다() {
        // given
        Tab rootTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode rootNode = TabNode.createRoot(rootTab);

        // when
        boolean isRoot = rootNode.isRoot();

        // then
        assertThat(isRoot).isTrue();
    }

    @Test
    void Tab의_id를_반환한다() {
        // given
        Tab tab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode node = TabNode.createRoot(tab);

        // when
        TabId actual = node.getId();

        // then
        assertThat(actual.getValue()).isEqualTo(1L);
    }

    @Test
    void 같은_Tab_id를_가진_TabNode는_동등하다() {
        // given
        Tab tab1 = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab tab2 = new Tab(
                TabId.create(1L),
                TabId.create(10L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode node1 = TabNode.createRoot(tab1);
        TabNode node2 = TabNode.create(tab2, 1);

        // when & then
        assertAll(
                () -> assertThat(node1).isEqualTo(node2),
                () -> assertThat(node1).hasSameHashCodeAs(node2)
        );
    }

    @Test
    void 다른_Tab_id를_가진_TabNode는_동등하지_않다() {
        // given
        Tab tab1 = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        Tab tab2 = new Tab(
                TabId.create(2L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode node1 = TabNode.createRoot(tab1);
        TabNode node2 = TabNode.createRoot(tab2);

        // when & then
        assertAll(
                () -> assertThat(node1).isNotEqualTo(node2),
                () -> assertThat(node1).doesNotHaveSameHashCodeAs(node2)
        );
    }

    @Test
    void 자식이_다르더라도_같은_Tab_id를_가지면_동등하다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode node1 = TabNode.createRoot(parentTab);
        TabNode node2 = TabNode.createRoot(parentTab);

        Tab childTab = new Tab(
                TabId.create(2L),
                TabId.create(1L),
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode childNode = TabNode.create(childTab, 1);
        node1.addChild(childNode);

        // when & then
        assertAll(
                () -> assertThat(node1).isEqualTo(node2),
                () -> assertThat(node1).hasSameHashCodeAs(node2)
        );
    }

    @Test
    void 탭_노드의_자식이_있는지_확인한다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(1L),
                TabId.EMPTY_TAB_ID,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("테스트 탭"),
                TabUrl.create("http://test.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );
        TabNode node = TabNode.createRoot(parentTab);

        // when
        boolean actual = node.isLeaf();

        // then
        assertThat(actual).isTrue();
    }
}
