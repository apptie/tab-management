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

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabTest {

    @Test
    void Tab을_초기화할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();

        // when
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        // then
        assertAll(
                () -> assertThat(tab).isNotNull(),
                () -> assertThat(tab.getId()).isEqualTo(tabId),
                () -> assertThat(tab.getParentId()).isEqualTo(parentId),
                () -> assertThat(tab.getTabGroupId()).isEqualTo(tabGroupId),
                () -> assertThat(tab.getTitle()).isEqualTo(title),
                () -> assertThat(tab.getUrl()).isEqualTo(url),
                () -> assertThat(tab.getPosition()).isEqualTo(position),
                () -> assertThat(tab.getTimestamps()).isEqualTo(timestamps)
        );
    }

    @Test
    void 제목과_URL을_업데이트할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        String newTitleValue = "업데이트된 제목";
        String newUrlValue = "https://updated.com";

        // when
        Tab updatedTab = tab.updateInfo(newTitleValue, newUrlValue);

        // then
        assertAll(
                () -> assertThat(updatedTab).isNotNull(),
                () -> assertThat(updatedTab.getId()).isEqualTo(tabId),
                () -> assertThat(updatedTab.getParentId()).isEqualTo(parentId),
                () -> assertThat(updatedTab.getTabGroupId()).isEqualTo(tabGroupId),
                () -> assertThat(updatedTab.getTitle().getValue()).isEqualTo(newTitleValue),
                () -> assertThat(updatedTab.getUrl().getValue()).isEqualTo(newUrlValue),
                () -> assertThat(updatedTab.getPosition()).isEqualTo(position),
                () -> assertThat(updatedTab.getUpdatedAt()).isAfter(timestamps.getUpdatedAt())
        );
    }

    @Test
    void Tab의_위치를_업데이트할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        int newPositionValue = 5;

        // when
        Tab updatedTab = tab.updatePosition(newPositionValue);

        // then
        assertAll(
                () -> assertThat(updatedTab).isNotNull(),
                () -> assertThat(updatedTab.getId()).isEqualTo(tabId),
                () -> assertThat(updatedTab.getParentId()).isEqualTo(parentId),
                () -> assertThat(updatedTab.getTabGroupId()).isEqualTo(tabGroupId),
                () -> assertThat(updatedTab.getTitle()).isEqualTo(title),
                () -> assertThat(updatedTab.getUrl()).isEqualTo(url),
                () -> assertThat(updatedTab.getPosition().getValue()).isEqualTo(newPositionValue),
                () -> assertThat(updatedTab.getUpdatedAt()).isAfter(timestamps.getUpdatedAt())
        );
    }

    @Test
    void Tab의_위치를_변경할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        TabId newParentId = TabId.create(20L);
        int newPositionValue = 2;

        // when
        Tab movedTab = tab.moveTo(newParentId, newPositionValue);

        // then
        assertAll(
                () -> assertThat(movedTab).isNotNull(),
                () -> assertThat(movedTab.getId()).isEqualTo(tabId),
                () -> assertThat(movedTab.getParentId()).isEqualTo(newParentId),
                () -> assertThat(movedTab.getTabGroupId()).isEqualTo(tabGroupId),
                () -> assertThat(movedTab.getTitle()).isEqualTo(title),
                () -> assertThat(movedTab.getUrl()).isEqualTo(url),
                () -> assertThat(movedTab.getPosition().getValue()).isEqualTo(newPositionValue),
                () -> assertThat(movedTab.getUpdatedAt()).isAfter(timestamps.getUpdatedAt())
        );
    }

    @Test
    void 부모가_없다면_루트_Tab이다() {
        // given
        TabId tabId = TabId.create(1L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab rootTab = new Tab(tabId, null, tabGroupId, title, url, position, timestamps);

        // when
        boolean isRoot = rootTab.isRoot();

        // then
        assertThat(isRoot).isTrue();
    }

    @Test
    void 부모가_있으면_루트_Tab이_아니다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        // when
        boolean isRoot = tab.isRoot();

        // then
        assertThat(isRoot).isFalse();
    }

    @Test
    void 같은_id를_가진_Tab인지_확인한다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();

        Tab tab1 = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);
        Tab tab2 = new Tab(tabId, TabId.create(999L), TabGroupId.create(999L),
                TabTitle.create("다른 제목"), TabUrl.create("https://different.com"),
                TabPosition.create(999), AuditTimestamps.now());

        // when
        boolean isEqualId = tab1.isEqualId(tab2);

        // then
        assertThat(isEqualId).isTrue();
    }

    @Test
    void 생성_시간을_조회할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        // when
        LocalDateTime createdAt = tab.getCreatedAt();

        // then
        assertAll(
                () -> assertThat(createdAt).isNotNull(),
                () -> assertThat(createdAt).isEqualTo(timestamps.getCreatedAt())
        );
    }

    @Test
    void 수정_시간을_조회할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        // when
        LocalDateTime updatedAt = tab.getUpdatedAt();

        // then
        assertAll(
                () -> assertThat(updatedAt).isNotNull(),
                () -> assertThat(updatedAt).isEqualTo(timestamps.getUpdatedAt())
        );
    }

    @Test
    void 같은_id를_가진_Tab은_동등하다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();

        Tab tab1 = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);
        Tab tab2 = new Tab(tabId, TabId.create(999L), TabGroupId.create(999L),
                TabTitle.create("다른 제목"), TabUrl.create("https://different.com"),
                TabPosition.create(999), AuditTimestamps.now());

        // when & then
        assertAll(
                () -> assertThat(tab1).isEqualTo(tab2),
                () -> assertThat(tab1).hasSameHashCodeAs(tab2)
        );
    }

    @Test
    void 다른_id를_가진_Tab은_동등하지_않다() {
        // given
        TabId tabId1 = TabId.create(1L);
        TabId tabId2 = TabId.create(2L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();

        Tab tab1 = new Tab(tabId1, parentId, tabGroupId, title, url, position, timestamps);
        Tab tab2 = new Tab(tabId2, parentId, tabGroupId, title, url, position, timestamps);

        // when & then
        assertAll(
                () -> assertThat(tab1).isNotEqualTo(tab2),
                () -> assertThat(tab1).doesNotHaveSameHashCodeAs(tab2)
        );
    }
}
