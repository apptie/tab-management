package com.management.tab.domain.content;

import com.management.tab.domain.content.vo.Content;
import com.management.tab.domain.content.vo.TabContentId;
import com.management.tab.domain.tab.vo.TabId;
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
        String content = "테스트 내용";

        // when
        TabContent tabContent = TabContent.create(tabId, content);

        // then
        assertAll(
                () -> assertThat(tabContent).isNotNull(),
                () -> assertThat(tabContent.getId()).isNull(),
                () -> assertThat(tabContent.getTabId()).isEqualTo(tabId),
                () -> assertThat(tabContent.getContent()).isEqualTo(Content.create(content)),
                () -> assertThat(tabContent.getAuditTimestamps()).isNotNull()
        );
    }

    @Test
    void 생성된_탭_컨텐츠에_ID를_부여할_수_있다() {
        // given
        TabContent original = TabContent.create(TabId.create(1L), "원본 내용");
        TabContentId newId = TabContentId.create(100L);

        // when
        TabContent updated = original.withId(newId);

        // then
        assertAll(
                () -> assertThat(updated).isNotNull(),
                () -> assertThat(updated.getId()).isEqualTo(newId),
                () -> assertThat(updated.getTabId()).isEqualTo(original.getTabId()),
                () -> assertThat(updated.getContent()).isEqualTo(original.getContent()),
                () -> assertThat(original.getId()).isNull()
        );
    }

    @Test
    void 탭_컨텐츠의_내용을_수정할_수_있다() {
        // given
        TabContent original = TabContent.create(TabId.create(1L), "원본 내용")
                                        .withId(TabContentId.create(100L));
        String newContent = "수정된 내용";

        // when
        TabContent updated = original.updateContent(newContent);

        // then
        assertAll(
                () -> assertThat(updated).isNotNull(),
                () -> assertThat(updated.getId()).isEqualTo(original.getId()),
                () -> assertThat(updated.getTabId()).isEqualTo(original.getTabId()),
                () -> assertThat(updated.getContent()).isEqualTo(Content.create(newContent)),
                () -> assertThat(updated.getContent()).isNotEqualTo(original.getContent())
        );
    }

    @Test
    void ID_부여_시_원본_탭_컨텐츠는_변경되지_않는다() {
        // given
        TabContent original = TabContent.create(TabId.create(1L), "원본 내용");
        TabContentId originalId = original.getId();
        Content originalContent = original.getContent();
        TabId originalTabId = original.getTabId();

        // when
        original.withId(TabContentId.create(100L));

        // then
        assertAll(
                () -> assertThat(original.getId()).isEqualTo(originalId),
                () -> assertThat(original.getContent()).isEqualTo(originalContent),
                () -> assertThat(original.getTabId()).isEqualTo(originalTabId)
        );
    }

    @Test
    void 내용_수정_시_원본_탭_컨텐츠는_변경되지_않는다() {
        // given
        TabContent original = TabContent.create(TabId.create(1L), "원본 내용")
                                        .withId(TabContentId.create(100L));
        TabContentId originalId = original.getId();
        Content originalContent = original.getContent();
        TabId originalTabId = original.getTabId();

        // when
        original.updateContent("수정된 내용");

        // then
        assertAll(
                () -> assertThat(original.getId()).isEqualTo(originalId),
                () -> assertThat(original.getContent()).isEqualTo(originalContent),
                () -> assertThat(original.getTabId()).isEqualTo(originalTabId)
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
}
