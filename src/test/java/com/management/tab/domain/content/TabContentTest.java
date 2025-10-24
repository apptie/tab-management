package com.management.tab.domain.content;

import com.management.tab.domain.common.AuditTimestamps;
import com.management.tab.domain.content.vo.Content;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.tab.vo.TabId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabContentTest {

    @Test
    void TabId와_내용으로_새로운_탭_컨텐츠를_생성할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);

        // when
        TabContent actual = TabContent.create(tabId, "테스트 내용");

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNull(),
                () -> assertThat(actual.getTabId()).isEqualTo(1L),
                () -> assertThat(actual.getContent()).isEqualTo("테스트 내용"),
                () -> assertThat(actual.getContent()).isNotNull()
        );
    }

    @Test
    void 생성된_탭_컨텐츠에_ID를_부여할_수_있다() {
        // given
        TabContent original = TabContent.create(TabId.create(1L), "원본 내용");
        TabContentId newId = TabContentId.create(100L);

        // when
        TabContent actual = original.withId(newId);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(100L),
                () -> assertThat(actual.getTabId()).isEqualTo(original.getTabId()),
                () -> assertThat(actual.getContent()).isEqualTo(original.getContent()),
                () -> assertThat(original.getId()).isNull()
        );
    }

    @Test
    void 탭_컨텐츠의_내용을_수정할_수_있다() {
        // given
        TabContent original = TabContent.create(TabId.create(1L), "원본 내용")
                                        .withId(TabContentId.create(100L));

        // when
        TabContent actual = original.updateContent("수정된 내용");

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(original.getId()),
                () -> assertThat(actual.getTabId()).isEqualTo(original.getTabId()),
                () -> assertThat(actual.getContent()).isEqualTo("수정된 내용")
        );
    }

    @Test
    void 같은_ID를_가진_탭_컨텐츠는_동등하다() {
        // given
        TabContentId sameId = TabContentId.create(1L);
        TabContent tabContent1 = TabContent.create(TabId.create(1L), "첫 번째 내용")
                                           .withId(sameId);
        TabContent tabContent2 = TabContent.create(TabId.create(2L), "두 번째 내용")
                                           .withId(sameId);

        // when & then
        assertAll(
                () -> assertThat(tabContent1).isEqualTo(tabContent2),
                () -> assertThat(tabContent1).hasSameHashCodeAs(tabContent2)
        );
    }

    @Test
    void 다른_ID를_가진_탭_컨텐츠는_동등하지_않다() {
        // given
        TabContent tabContent1 = TabContent.create(TabId.create(1L), "같은 내용")
                                           .withId(TabContentId.create(1L));
        TabContent tabContent2 = TabContent.create(TabId.create(1L), "같은 내용")
                                           .withId(TabContentId.create(2L));

        // when & then
        assertAll(
                () -> assertThat(tabContent1).isNotEqualTo(tabContent2),
                () -> assertThat(tabContent1).doesNotHaveSameHashCodeAs(tabContent2)
        );
    }

    @Test
    void ID가_없는_탭_컨텐츠와_ID가_있는_탭_컨텐츠는_동등하지_않다() {
        // given
        TabContent tabContentWithoutId = TabContent.create(TabId.create(1L), "같은 내용");
        TabContent tabContentWithId = TabContent.create(TabId.create(1L), "같은 내용")
                                                .withId(TabContentId.create(1L));

        // when & then
        assertThat(tabContentWithoutId).isNotEqualTo(tabContentWithId);
    }

    @Test
    void ID가_포함된_탭_컨텐츠를_초기화할_수_있다() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);

        // when
        TabContent actual = TabContent.create(
                TabContentId.create(100L),
                TabId.create(1L),
                Content.create("직접 생성 테스트"),
                AuditTimestamps.create(createdAt, updatedAt)
        );

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(100L),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(createdAt),
                () -> assertThat(actual.getUpdatedAt()).isEqualTo(updatedAt)
        );
    }
}
