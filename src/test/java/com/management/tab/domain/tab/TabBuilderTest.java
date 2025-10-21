package com.management.tab.domain.tab;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.tab.vo.GroupId;
import com.management.tab.domain.tab.vo.TabId;
import com.management.tab.domain.tab.vo.TabPosition;
import com.management.tab.domain.tab.vo.TabTitle;
import com.management.tab.domain.tab.vo.TabUrl;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabBuilderTest {

    @Test
    void builder로_빌더를_생성할_수_있다() {
        // when
        TabBuilder builder = TabBuilder.builder();

        // then
        assertThat(builder).isNotNull();
    }

    @Test
    void createRoot로_루트_탭_빌더를_생성할_수_있다() {
        // given
        Long groupId = 1L;
        String title = "루트 탭";
        String url = "https://example.com";

        // when
        TabBuilder builder = TabBuilder.createRoot(groupId, title, url);

        // then
        assertThat(builder).isNotNull();
    }

    @Test
    void createRoot로_생성한_빌더로_루트_탭을_build할_수_있다() {
        // given
        Long groupId = 1L;
        String title = "루트 탭";
        String url = "https://example.com";

        // when
        Tab tab = TabBuilder.createRoot(groupId, title, url)
                            .position(0)
                            .build();

        // then
        assertAll(
                () -> assertThat(tab).isNotNull(),
                () -> assertThat(tab.getGroupId().getValue()).isEqualTo(groupId),
                () -> assertThat(tab.getTitle().getValue()).isEqualTo(title),
                () -> assertThat(tab.getUrl().getValue()).isEqualTo(url),
                () -> assertThat(tab.getParentId()).isNull(),
                () -> assertThat(tab.isRoot()).isTrue()
        );
    }

    @Test
    void createChild로_자식_탭_빌더를_생성할_수_있다() {
        // given
        GroupId groupId = GroupId.create(1L);
        TabId parentId = TabId.create(10L);
        String title = "자식 탭";
        String url = "https://child.com";

        // when
        TabBuilder builder = TabBuilder.createChild(groupId, parentId, title, url);

        // then
        assertThat(builder).isNotNull();
    }

    @Test
    void createChild로_생성한_빌더로_자식_탭을_build할_수_있다() {
        // given
        GroupId groupId = GroupId.create(1L);
        TabId parentId = TabId.create(10L);
        String title = "자식 탭";
        String url = "https://child.com";

        // when
        Tab tab = TabBuilder.createChild(groupId, parentId, title, url)
                            .position(0)
                            .build();

        // then
        assertAll(
                () -> assertThat(tab).isNotNull(),
                () -> assertThat(tab.getGroupId()).isEqualTo(groupId),
                () -> assertThat(tab.getParentId()).isEqualTo(parentId),
                () -> assertThat(tab.getTitle().getValue()).isEqualTo(title),
                () -> assertThat(tab.getUrl().getValue()).isEqualTo(url),
                () -> assertThat(tab.isRoot()).isFalse()
        );
    }

    @Test
    void createChild에서_parentId가_null이면_예외가_발생한다() {
        // given
        GroupId groupId = GroupId.create(1L);
        TabId parentId = null;
        String title = "자식 탭";
        String url = "https://child.com";

        // when & then
        assertThatThrownBy(() -> TabBuilder.createChild(groupId, parentId, title, url))
                .isInstanceOf(NullPointerException.class)
                .hasMessage("부모 ID는 필수입니다.");
    }

    @Test
    void createWithAssignedId로_기존_Tab에서_ID를_할당한_빌더를_생성할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        TabId parentId = TabId.create(10L);
        GroupId groupId = GroupId.create(100L);
        TabTitle title = TabTitle.create("기존 탭");
        TabUrl url = TabUrl.create("https://existing.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab existingTab = new Tab(tabId, parentId, groupId, title, url, position, timestamps);

        Long newTabId = 999L;

        // when
        Tab newTab = TabBuilder.createWithAssignedId(newTabId, existingTab).build();

        // then
        assertAll(
                () -> assertThat(newTab).isNotNull(),
                () -> assertThat(newTab.getId().getValue()).isEqualTo(newTabId),
                () -> assertThat(newTab.getParentId()).isEqualTo(existingTab.getParentId()),
                () -> assertThat(newTab.getGroupId()).isEqualTo(existingTab.getGroupId()),
                () -> assertThat(newTab.getTitle()).isEqualTo(existingTab.getTitle()),
                () -> assertThat(newTab.getUrl()).isEqualTo(existingTab.getUrl()),
                () -> assertThat(newTab.getPosition()).isEqualTo(existingTab.getPosition()),
                () -> assertThat(newTab.getTimestamps()).isEqualTo(existingTab.getTimestamps())
        );
    }

    @Test
    void parentId로_부모_ID를_설정할_수_있다() {
        // given
        Long parentIdValue = 10L;

        // when
        Tab tab = TabBuilder.builder()
                            .parentId(parentIdValue)
                            .groupId(1L)
                            .title("테스트 탭")
                            .url("https://test.com")
                            .position(0)
                            .build();

        // then
        assertThat(tab.getParentId().getValue()).isEqualTo(parentIdValue);
    }

    @Test
    void groupId로_그룹_ID를_설정할_수_있다() {
        // given
        Long groupIdValue = 100L;

        // when
        Tab tab = TabBuilder.builder()
                            .groupId(groupIdValue)
                            .title("테스트 탭")
                            .url("https://test.com")
                            .position(0)
                            .build();

        // then
        assertThat(tab.getGroupId().getValue()).isEqualTo(groupIdValue);
    }

    @Test
    void title로_제목을_설정할_수_있다() {
        // given
        String titleValue = "테스트 제목";

        // when
        Tab tab = TabBuilder.builder()
                            .groupId(1L)
                            .title(titleValue)
                            .url("https://test.com")
                            .position(0)
                            .build();

        // then
        assertThat(tab.getTitle().getValue()).isEqualTo(titleValue);
    }

    @Test
    void url로_URL을_설정할_수_있다() {
        // given
        String urlValue = "https://example.com";

        // when
        Tab tab = TabBuilder.builder()
                            .groupId(1L)
                            .title("테스트 탭")
                            .url(urlValue)
                            .position(0)
                            .build();

        // then
        assertThat(tab.getUrl().getValue()).isEqualTo(urlValue);
    }

    @Test
    void position으로_위치를_설정할_수_있다() {
        // given
        int positionValue = 5;

        // when
        Tab tab = TabBuilder.builder()
                            .groupId(1L)
                            .title("테스트 탭")
                            .url("https://test.com")
                            .position(positionValue)
                            .build();

        // then
        assertThat(tab.getPosition().getValue()).isEqualTo(positionValue);
    }

    @Test
    void position을_설정하지_않으면_기본값이_설정된다() {
        // when
        Tab tab = TabBuilder.builder()
                            .groupId(1L)
                            .title("테스트 탭")
                            .url("https://test.com")
                            .build();

        // then
        assertThat(tab.getPosition()).isEqualTo(TabPosition.defaultPosition());
    }

    @Test
    void timestamps를_설정하지_않으면_현재_시간이_설정된다() {
        // when
        Tab tab = TabBuilder.builder()
                            .groupId(1L)
                            .title("테스트 탭")
                            .url("https://test.com")
                            .position(0)
                            .build();

        // then
        assertAll(
                () -> assertThat(tab.getCreatedAt()).isNotNull(),
                () -> assertThat(tab.getUpdatedAt()).isNotNull()
        );
    }

    @Test
    void groupId가_없으면_build_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> TabBuilder.builder()
                                           .title("테스트 탭")
                                           .url("https://test.com")
                                           .position(0)
                                           .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("그룹 ID는 필수입니다.");
    }

    @Test
    void title이_없으면_build_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> TabBuilder.builder()
                                           .groupId(1L)
                                           .url("https://test.com")
                                           .position(0)
                                           .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("제목은 필수입니다.");
    }

    @Test
    void url이_없으면_build_시_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> TabBuilder.builder()
                                           .groupId(1L)
                                           .title("테스트 탭")
                                           .position(0)
                                           .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Url은 필수입니다.");
    }

    @Test
    void 메서드_체이닝으로_Tab을_생성할_수_있다() {
        // given
        Long parentIdValue = 10L;
        Long groupIdValue = 100L;
        String titleValue = "체이닝 테스트";
        String urlValue = "https://chaining.com";
        int positionValue = 3;

        // when
        Tab tab = TabBuilder.builder()
                            .parentId(parentIdValue)
                            .groupId(groupIdValue)
                            .title(titleValue)
                            .url(urlValue)
                            .position(positionValue)
                            .build();

        // then
        assertAll(
                () -> assertThat(tab).isNotNull(),
                () -> assertThat(tab.getParentId().getValue()).isEqualTo(parentIdValue),
                () -> assertThat(tab.getGroupId().getValue()).isEqualTo(groupIdValue),
                () -> assertThat(tab.getTitle().getValue()).isEqualTo(titleValue),
                () -> assertThat(tab.getUrl().getValue()).isEqualTo(urlValue),
                () -> assertThat(tab.getPosition().getValue()).isEqualTo(positionValue)
        );
    }

    @Test
    void createRoot는_parentId_없이_Tab을_생성한다() {
        // given
        Long groupId = 1L;
        String title = "루트 탭";
        String url = "https://root.com";

        // when
        Tab tab = TabBuilder.createRoot(groupId, title, url)
                            .position(0)
                            .build();

        // then
        assertAll(
                () -> assertThat(tab.getParentId()).isNull(),
                () -> assertThat(tab.isRoot()).isTrue()
        );
    }

    @Test
    void createRoot_이후_parentId를_설정하면_자식_탭으로_변경할_수_있다() {
        // given
        Long groupId = 1L;
        String title = "루트에서 자식으로";
        String url = "https://change.com";
        Long parentIdValue = 10L;

        // when
        Tab tab = TabBuilder.createRoot(groupId, title, url)
                            .parentId(parentIdValue)
                            .position(0)
                            .build();

        // then
        assertAll(
                () -> assertThat(tab.getParentId()).isNotNull(),
                () -> assertThat(tab.getParentId().getValue()).isEqualTo(parentIdValue),
                () -> assertThat(tab.isRoot()).isFalse()
        );
    }
}
