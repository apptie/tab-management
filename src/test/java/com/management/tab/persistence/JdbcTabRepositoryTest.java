package com.management.tab.persistence;

import com.management.tab.domain.group.vo.TabGroupId;
import com.management.tab.domain.tab.Tab;
import com.management.tab.domain.tab.TabBuilder;
import com.management.tab.domain.tab.TabTree;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/insert-jdbc-tab-repository-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class JdbcTabRepositoryTest {

    @Autowired
    private JdbcTabRepository jdbcTabRepository;

    @Test
    void 루트_탭을_저장할_수_있다() {
        // given
        Tab rootTab = TabBuilder.createRoot(1L, "새 루트 탭", "https://new-root.com", TabPosition.create(10))
                                .build();

        // when
        Tab actual = jdbcTabRepository.saveRoot(rootTab);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getTitle().getValue()).isEqualTo("새 루트 탭"),
                () -> assertThat(actual.getUrl().getValue()).isEqualTo("https://new-root.com"),
                () -> assertThat(actual.getPosition().getValue()).isEqualTo(10),
                () -> assertThat(actual.isRoot()).isTrue()
        );
    }

    @Test
    void 자식_탭을_저장할_수_있다() {
        // given
        Tab parentTab = jdbcTabRepository.findTab(100L);
        Tab childTab = TabBuilder.createChild(parentTab, "새 자식 탭", "https://new-child.com", TabPosition.create(5))
                                 .build();

        // when
        Tab actual = jdbcTabRepository.saveChild(childTab);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getTitle().getValue()).isEqualTo("새 자식 탭"),
                () -> assertThat(actual.getUrl().getValue()).isEqualTo("https://new-child.com"),
                () -> assertThat(actual.getParentId().getValue()).isEqualTo(100L),
                () -> assertThat(actual.isRoot()).isFalse()
        );
    }

    @Test
    void ID로_탭을_조회할_수_있다() {
        // given
        Long tabId = 100L;

        // when
        Tab actual = jdbcTabRepository.findTab(tabId);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId().getValue()).isEqualTo(100L),
                () -> assertThat(actual.getTitle().getValue()).isEqualTo("루트_탭1")
        );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_예외가_발생한다() {
        // given
        Long tabId = 999L;

        // when & then
        assertThatThrownBy(() -> jdbcTabRepository.findTab(tabId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지정한 탭을 찾을 수 없습니다.");
    }

    @Test
    void 그룹의_탭_트리를_조회할_수_있다() {
        // given
        TabGroupId groupId = TabGroupId.create(1L);

        // when
        TabTree actual = jdbcTabRepository.findTabTree(groupId);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTabGroupId()).isEqualTo(groupId),
                () -> assertThat(actual.getTotalCount()).isEqualTo(6)
        );
    }

    @Test
    void 탭의_부모_ID를_조회할_수_있다() {
        // given
        Long tabId = 101L;

        // when
        TabId actual = jdbcTabRepository.findParentId(tabId);

        // then
        assertThat(actual.getValue()).isEqualTo(100L);
    }

    @Test
    void 루트_탭의_부모_ID를_조회하면_null을_반환한다() {
        // given
        Long rootTabId = 100L;

        // when
        TabId actual = jdbcTabRepository.findParentId(rootTabId);

        // then
        assertThat(actual).isNull();
    }

    @Test
    void 그룹의_마지막_루트_탭_위치를_조회할_수_있다() {
        // given
        Long groupId = 1L;

        // when
        TabPosition actual = jdbcTabRepository.findLastRootPosition(groupId);

        // then
        assertThat(actual.getValue()).isZero();
    }

    @Test
    void 특정_부모의_형제_탭들을_조회할_수_있다() {
        // given
        TabId parentId = TabId.create(100L);

        // when
        List<Tab> actual = jdbcTabRepository.findSiblings(parentId);

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 형제_탭_조회_시_모두_같은_부모를_가진다() {
        // given
        TabId parentId = TabId.create(100L);

        // when
        List<Tab> actual = jdbcTabRepository.findSiblings(parentId);

        // then
        assertThat(actual).isNotEmpty()
                          .allMatch(tab -> tab.getParentId().getValue().equals(100L));
    }

    @Test
    void 루트_레벨_형제들을_조회할_수_있다() {
        // when
        List<Tab> actual = jdbcTabRepository.findRootSiblings();

        // then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void 루트_레벨_형제_조회_시_모두_루트_탭이다() {
        // when
        List<Tab> actual = jdbcTabRepository.findRootSiblings();

        // then
        assertThat(actual).isNotEmpty()
                          .allMatch(Tab::isRoot);
    }

    @Test
    void 탭을_다른_부모로_이동할_수_있다() {
        // given
        Tab tab = jdbcTabRepository.findTab(103L);
        TabId currentParentId = TabId.create(101L);
        Tab movedTab = tab.moveTo(TabId.create(102L), TabPosition.create(0));

        // when
        jdbcTabRepository.updateMoved(movedTab, currentParentId);

        // then
        Tab actual = jdbcTabRepository.findTab(103L);
        assertThat(actual.getParentId().getValue()).isEqualTo(102L);
    }

    @Test
    void 탭을_서브트리와_함께_이동할_수_있다() {
        // given
        Tab tab = jdbcTabRepository.findTab(101L);
        Tab movedTab = tab.moveTo(TabId.create(102L), TabPosition.create(0));

        // when
        jdbcTabRepository.updateMovedTabWithSubtree(movedTab);

        // then
        Tab actual = jdbcTabRepository.findTab(101L);
        assertThat(actual.getParentId().getValue()).isEqualTo(102L);
    }

    @Test
    void 탭을_루트로_이동할_수_있다() {
        // given
        Tab tab = jdbcTabRepository.findTab(101L);
        TabId currentParentId = TabId.create(100L);
        Tab movedTab = tab.moveToRoot(TabPosition.create(1));

        // when
        jdbcTabRepository.updateMovedRoot(movedTab, currentParentId);

        // then
        Tab actual = jdbcTabRepository.findTab(101L);

        assertAll(
                () -> assertThat(actual.getParentId()).isNull(),
                () -> assertThat(actual.isRoot()).isTrue()
        );
    }

    @Test
    void 탭을_서브트리와_함께_루트로_이동할_수_있다() {
        // given
        Tab tab = jdbcTabRepository.findTab(101L);
        Tab movedTab = tab.moveToRoot(TabPosition.create(1));

        // when
        jdbcTabRepository.updateMovedRootWithSubtree(movedTab);

        // then
        Tab actual = jdbcTabRepository.findTab(101L);
        assertThat(actual.getParentId()).isNull();
    }

    @Test
    void 탭의_위치를_변경할_수_있다() {
        // given
        TabId tabId = TabId.create(101L);
        TabPosition newPosition = TabPosition.create(10);

        // when
        jdbcTabRepository.updatePosition(tabId, newPosition);

        // then
        Tab actual = jdbcTabRepository.findTab(101L);
        assertThat(actual.getPosition().getValue()).isEqualTo(10);
    }

    @Test
    void 탭의_정보를_수정할_수_있다() {
        // given
        Tab tab = jdbcTabRepository.findTab(100L);
        Tab updatedTab = tab.updateInfo("수정된 제목", "https://updated.com");

        // when
        jdbcTabRepository.updateTabInfo(updatedTab);

        // then
        Tab actual = jdbcTabRepository.findTab(100L);
        assertAll(
                () -> assertThat(actual.getTitle().getValue()).isEqualTo("수정된 제목"),
                () -> assertThat(actual.getUrl().getValue()).isEqualTo("https://updated.com")
        );
    }

    @Test
    void 탭을_서브트리와_함께_삭제할_수_있다() {
        // given
        Tab tab = jdbcTabRepository.findTab(101L);
        TabTree beforeTree = jdbcTabRepository.findTabTree(TabGroupId.create(1L));
        int beforeCount = beforeTree.getTotalCount();

        // when
        jdbcTabRepository.deleteTabWithSubtree(tab);

        // then
        TabTree actual = jdbcTabRepository.findTabTree(TabGroupId.create(1L));
        assertThat(actual.getTotalCount()).isLessThan(beforeCount);
    }

    @Test
    void 탭을_서브트리와_함께_삭제하면_해당_탭의_자손도_모두_삭제된다() {
        // given
        Tab tab = jdbcTabRepository.findTab(101L);

        // when
        jdbcTabRepository.deleteTabWithSubtree(tab);

        // then
        assertAll(
                () -> assertThatThrownBy(() -> jdbcTabRepository.findTab(101L))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> jdbcTabRepository.findTab(103L))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> assertThatThrownBy(() -> jdbcTabRepository.findTab(104L))
                        .isInstanceOf(IllegalArgumentException.class)
        );
    }

    @Test
    void 해당_탭만_삭제할_수_있다() {
        // given
        Tab tab = jdbcTabRepository.findTab(105L);

        // when
        jdbcTabRepository.deleteTab(tab);

        // then
        assertThatThrownBy(() -> jdbcTabRepository.findTab(105L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지정한 탭을 찾을 수 없습니다.");
    }

    @Test
    void 해당_탭만_삭제하면_자식은_유지된다() {
        // given
        Tab tab = jdbcTabRepository.findTab(102L);

        // when
        jdbcTabRepository.deleteTab(tab);

        // then
        assertAll(
                () -> assertThatThrownBy(() -> jdbcTabRepository.findTab(102L))
                        .isInstanceOf(IllegalArgumentException.class),
                () -> jdbcTabRepository.findTab(105L)
        );
    }
}
