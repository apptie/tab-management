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
class TabBuilderTest {

    @Test
    void 루트_탭을_초기화할_수_있다() {
        // when
        Tab actual = TabBuilder.createRoot(
                                       1L,
                                       2L,
                                       "루트 탭",
                                       "https://example.com",
                                       TabPosition.create(0)
                               )
                               .build();

        // then
        assertAll(
                () -> assertThat(actual.getTabGroupId()).isEqualTo(1L),
                () -> assertThat(actual.getWriterId()).isEqualTo(2L),
                () -> assertThat(actual.getTitle()).isEqualTo("루트 탭"),
                () -> assertThat(actual.getUrl()).isEqualTo("https://example.com"),
                () -> assertThat(actual.getPosition()).isZero(),
                () -> assertThat(actual.parentId()).isSameAs(TabId.EMPTY_TAB_ID),
                () -> assertThat(actual.isRoot()).isTrue()
        );
    }

    @Test
    void 자식_탭을_초기화할_수_있다() {
        // given
        Tab parentTab = new Tab(
                TabId.create(10L),
                null,
                UserId.create(2L),
                TabGroupId.create(1L),
                TabTitle.create("부모 탭"),
                TabUrl.create("https://parent.com"),
                TabPosition.create(0),
                AuditTimestamps.now()
        );

        // when
        Tab actual = TabBuilder.createChild(parentTab, "자식 탭", "https://child.com", TabPosition.create(0))
                               .build();

        // then
        assertAll(
                () -> assertThat(actual.getTabGroupId()).isEqualTo(1L),
                () -> assertThat(actual.getWriterId()).isEqualTo(2L),
                () -> assertThat(actual.getParentId()).isEqualTo(10L),
                () -> assertThat(actual.getTitle()).isEqualTo("자식 탭"),
                () -> assertThat(actual.getUrl()).isEqualTo("https://child.com"),
                () -> assertThat(actual.getPosition()).isZero(),
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
                               .writerId(2L)
                               .build();

        // then
        assertThat(actual.position()).isEqualTo(TabPosition.defaultPosition());
    }

    @Test
    void timestamps를_설정하지_않으면_현재_시간이_설정된다() {
        // when
        Tab actual = TabBuilder.builder()
                               .groupId(1L)
                               .title("테스트 탭")
                               .url("https://test.com")
                               .writerId(2L)
                               .position(0)
                               .build();

        // then
        assertAll(
                () -> assertThat(actual.getCreatedAt()).isNotNull(),
                () -> assertThat(actual.getUpdatedAt()).isNotNull()
        );
    }

    @Test
    void 그룹_ID가_없으면_탭을_초기화_할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabBuilder.builder()
                                           .title("테스트 탭")
                                           .url("https://test.com")
                                           .position(0)
                                           .writerId(2L)
                                           .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("그룹 ID는 필수입니다.");
    }

    @Test
    void 제목이_없으면_탭을_초기화_할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabBuilder.builder()
                                           .groupId(1L)
                                           .url("https://test.com")
                                           .position(0)
                                           .writerId(2L)
                                           .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("제목은 필수입니다.");
    }

    @Test
    void URL이_없으면_탭을_초기화_할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabBuilder.builder()
                                           .groupId(1L)
                                           .title("테스트 탭")
                                           .position(0)
                                           .writerId(2L)
                                           .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Url은 필수입니다.");
    }

    @Test
    void writerI가_없으면_탭을_초기화_할_수_없다() {
        // when & then
        assertThatThrownBy(() -> TabBuilder.builder()
                                           .groupId(1L)
                                           .title("테스트 탭")
                                           .position(0)
                                           .writerId(2L)
                                           .build())
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Url은 필수입니다.");
    }

    @Test
    void 루트_탭은_부모_ID_없이_초기화된다() {
        // when
        Tab actual = TabBuilder.createRoot(1L, 2L, "루트 탭", "https://root.com", TabPosition.create(0))
                               .build();

        // then
        assertAll(
                () -> assertThat(actual.parentId()).isSameAs(TabId.EMPTY_TAB_ID),
                () -> assertThat(actual.isRoot()).isTrue()
        );
    }
}
