package com.management.tab.application;

import com.management.tab.domain.content.TabContent;
import com.management.tab.domain.tab.vo.TabId;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/service/tab-content-service-test-data.sql"})
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabContentServiceTest {

    @Autowired
    TabContentService tabContentService;

    @Test
    void 탭_ID로_모든_컨텐츠를_조회한다() {
        // when
        List<TabContent> actual = tabContentService.getAllContentsByTabId(TabId.create(1L));

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void ID로_특정_컨텐츠를_조회한다() {
        // when
        TabContent actual = tabContentService.getContent(1L);

        // then
        assertThat(actual.getContent()).isEqualTo("Spring 프레임워크 학습 내용");
    }

    @Test
    void 새로운_컨텐츠를_초기화한다() {
        // when
        Long actual = tabContentService.createContent(TabId.create(2L), "새로운 컨텐츠");

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 컨텐츠_내용을_변경한다() {
        // when
        tabContentService.updateContent(1L, "변경된 내용");

        // then
        TabContent actual = tabContentService.getContent(1L);

        assertThat(actual.getContent()).isEqualTo("변경된 내용");
    }

    @Test
    void 컨텐츠를_삭제한다() {
        // when
        tabContentService.delete(3L);

        // then
        List<TabContent> actual = tabContentService.getAllContentsByTabId(TabId.create(2L));

        assertThat(actual).isEmpty();
    }

    @Test
    void 탭_ID로_모든_컨텐츠를_삭제한다() {
        // when
        tabContentService.deleteAllByTabId(TabId.create(1L));

        // then
        List<TabContent> actual = tabContentService.getAllContentsByTabId(TabId.create(1L));

        assertThat(actual).isEmpty();
    }

    @Test
    void 탭이_가지고_있는_컨텐츠_개수를_조회한다() {
        // when
        int actual = tabContentService.countContents(TabId.create(1L));

        // then
        assertThat(actual).isEqualTo(2);
    }

    @ParameterizedTest(name = "{0}일 때 컨텐츠를 초기화할 수 없다")
    @NullAndEmptySource
    void 빈_내용으로_컨텐츠를_초기화할_수_없다(String content) {
        // when & then
        assertThatThrownBy(() -> tabContentService.createContent(TabId.create(1L), content))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Content는 비어 있을 수 없습니다.");
    }
}
