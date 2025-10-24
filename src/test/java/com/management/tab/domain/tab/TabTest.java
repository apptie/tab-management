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
        TabPosition position = TabPosition.defaultPosition();
        AuditTimestamps timestamps = AuditTimestamps.now();

        // when
        Tab actual = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(tabId),
                () -> assertThat(actual.parentId()).isEqualTo(parentId),
                () -> assertThat(actual.tabGroupId()).isEqualTo(tabGroupId),
                () -> assertThat(actual.getTitle()).isEqualTo("테스트 탭"),
                () -> assertThat(actual.getUrl()).isEqualTo("https://example.com"),
                () -> assertThat(actual.getPosition()).isZero(),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(timestamps.getCreatedAt()),
                () -> assertThat(actual.getUpdatedAt()).isEqualTo(timestamps.getUpdatedAt())
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
        TabPosition position = TabPosition.defaultPosition();
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        String newTitleValue = "업데이트된 제목";
        String newUrlValue = "https://updated.com";

        // when
        Tab actual = tab.updateInfo(newTitleValue, newUrlValue);

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(tabId),
                () -> assertThat(actual.getTitle()).isEqualTo("업데이트된 제목"),
                () -> assertThat(actual.getUrl()).isEqualTo("https://updated.com"),
                () -> assertThat(actual.getUpdatedAt()).isAfter(timestamps.getUpdatedAt())
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
        Tab actual = tab.updatePosition(newPositionValue);

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(tabId),
                () -> assertThat(actual.getPosition()).isEqualTo(newPositionValue),
                () -> assertThat(actual.getUpdatedAt()).isAfter(timestamps.getUpdatedAt())
        );
    }

    @Test
    void Tab을_다른_부모로_이동할_수_있다() {
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
        TabPosition newPosition = TabPosition.create(2);

        // when
        Tab actual = tab.moveTo(newParentId, newPosition);

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(tabId),
                () -> assertThat(actual.parentId()).isEqualTo(newParentId),
                () -> assertThat(actual.position()).isEqualTo(newPosition),
                () -> assertThat(actual.getUpdatedAt()).isAfter(timestamps.getUpdatedAt())
        );
    }

    @Test
    void Tab을_루트로_이동할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(tabId, parentId, tabGroupId, title, url, position, timestamps);

        TabPosition newPosition = TabPosition.create(3);

        // when
        Tab actual = tab.moveToRoot(newPosition);

        // then
        assertAll(
                () -> assertThat(actual.id()).isEqualTo(tabId),
                () -> assertThat(actual.parentId()).isSameAs(TabId.EMPTY_TAB_ID),
                () -> assertThat(actual.getUpdatedAt()).isAfter(timestamps.getUpdatedAt())
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
        Tab rootTab = new Tab(tabId, TabId.EMPTY_TAB_ID, tabGroupId, title, url, position, timestamps);

        // when
        boolean actual = rootTab.isRoot();

        // then
        assertThat(actual).isTrue();
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
        boolean actual = tab.isRoot();

        // then
        assertThat(actual).isFalse();
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
        Tab tab2 = new Tab(
                tabId,
                TabId.create(999L),
                TabGroupId.create(999L),
                TabTitle.create("다른 제목"),
                TabUrl.create("https://different.com"),
                TabPosition.create(999),
                AuditTimestamps.now()
        );

        // when
        boolean actual = tab1.isEqualId(tab2);

        // then
        assertThat(actual).isTrue();
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
        LocalDateTime actual = tab.getCreatedAt();

        // then
        assertThat(actual).isEqualTo(timestamps.getCreatedAt());
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
        LocalDateTime actual = tab.getUpdatedAt();

        // then
        assertThat(actual).isEqualTo(timestamps.getUpdatedAt());
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

    @Test
    void TabId로_같은_Tab인지_확인한다() {
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
        boolean actual = tab.isEqualId(tabId);

        // then
        assertThat(actual).isTrue();
    }

    @Test
    void 기존_탭에서_ID를_업데이트_할_수_있다() {
        // given
        TabId parentId = TabId.create(10L);
        TabGroupId tabGroupId = TabGroupId.create(100L);
        TabTitle title = TabTitle.create("테스트 탭");
        TabUrl url = TabUrl.create("https://example.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab tab = new Tab(null, parentId, tabGroupId, title, url, position, timestamps);

        // when
        Tab actual = tab.updateAssignedId(1L);

        // then
        assertThat(actual.getId()).isEqualTo(1L);
    }
}
