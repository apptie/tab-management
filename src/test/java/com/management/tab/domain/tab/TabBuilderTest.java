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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabBuilderTest {

    @Test
    void 루트_탭을_초기화할_수_있다() {
        // given
        Long groupId = 1L;
        String title = "루트 탭";
        String url = "https://example.com";
        TabPosition position = TabPosition.create(0);

        // when
        Tab actual = TabBuilder.createRoot(groupId, title, url, position)
                               .build();

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTabGroupId().getValue()).isEqualTo(groupId),
                () -> assertThat(actual.getTitle().getValue()).isEqualTo(title),
                () -> assertThat(actual.getUrl().getValue()).isEqualTo(url),
                () -> assertThat(actual.getPosition().getValue()).isZero(),
                () -> assertThat(actual.getParentId()).isNull(),
                () -> assertThat(actual.isRoot()).isTrue()
        );
    }

    @Test
    void 자식_탭을_초기화할_수_있다() {
        // given
        TabGroupId tabGroupId = TabGroupId.create(1L);
        TabId parentId = null;
        TabTitle title = TabTitle.create("부모 탭");
        TabUrl url = TabUrl.create("https://parent.com");
        TabPosition position = TabPosition.create(0);
        AuditTimestamps timestamps = AuditTimestamps.now();
        Tab parentTab = new Tab(TabId.create(10L), parentId, tabGroupId, title, url, position, timestamps);

        String childTitle = "자식 탭";
        String childUrl = "https://child.com";
        TabPosition childPosition = TabPosition.create(0);

        // when
        Tab actual = TabBuilder.createChild(parentTab, childTitle, childUrl, childPosition)
                               .build();

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getTabGroupId()).isEqualTo(tabGroupId),
                () -> assertThat(actual.getParentId().getValue()).isEqualTo(10L),
                () -> assertThat(actual.getTitle().getValue()).isEqualTo(childTitle),
                () -> assertThat(actual.getUrl().getValue()).isEqualTo(childUrl),
                () -> assertThat(actual.getPosition().getValue()).isZero(),
                () -> assertThat(actual.isRoot()).isFalse()
        );
    }

    @Test
    void position을_설정하지_않으면_기본값이_설정된다() {
        // when
        Tab actual = TabBuilder.builder()
                               .groupId(1L)
                               .title("테스트 탭")
                               .url("https://test.com")
                               .build();

        // then
        assertThat(actual.getPosition()).isEqualTo(TabPosition.defaultPosition());
    }

    @Test
    void timestamps를_설정하지_않으면_현재_시간이_설정된다() {
        // when
        Tab actual = TabBuilder.builder()
                               .groupId(1L)
                               .title("테스트 탭")
                               .url("https://test.com")
                               .position(0)
                               .build();

        // then
        assertAll(
                () -> assertThat(actual.getCreatedAt()).isNotNull(),
                () -> assertThat(actual.getUpdatedAt()).isNotNull()
        );
    }

    @Test
    void 그룹_ID가_없으면_탭_초기화시_예외가_발생한다() {
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
    void 제목이_없으면_탭_초기화시_예외가_발생한다() {
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
    void URL이_없으면_탭_초기화시_예외가_발생한다() {
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
    void 루트_탭은_부모_ID_없이_초기화된다() {
        // given
        Long groupId = 1L;
        String title = "루트 탭";
        String url = "https://root.com";
        TabPosition position = TabPosition.create(0);

        // when
        Tab actual = TabBuilder.createRoot(groupId, title, url, position)
                               .build();

        // then
        assertAll(
                () -> assertThat(actual.getParentId()).isNull(),
                () -> assertThat(actual.isRoot()).isTrue()
        );
    }
}
