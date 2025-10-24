package com.management.tab.persistence;

import com.management.tab.domain.content.TabContent;
import com.management.tab.domain.repository.TabContentRepository.TabContentNotFoundException;
import com.management.tab.domain.tab.vo.TabId;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/insert-jdbc-tab-content-repository-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class JdbcTabContentRepositoryTest {

    @Autowired
    JdbcTabContentRepository jdbcTabContentRepository;

    @Test
    void 탭_ID로_모든_컨텐츠를_조회할_수_있다() {
        // when
        List<TabContent> actual = jdbcTabContentRepository.findAllByTabId(TabId.create(1L));

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 컨텐츠가_없는_탭_ID로_조회하면_빈_리스트를_반환한다() {
        // when
        List<TabContent> actual = jdbcTabContentRepository.findAllByTabId(TabId.create(3L));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void ID로_탭_컨텐츠를_조회할_수_있다() {
        // when
        TabContent actual = jdbcTabContentRepository.findById(1L);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(1L),
                () -> assertThat(actual.getTabId()).isEqualTo(1L),
                () -> assertThat(actual.getContent()).isEqualTo("Spring 프레임워크 학습 내용")
        );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_예외가_발생한다() {
        // when & then
        assertThatThrownBy(() -> jdbcTabContentRepository.findById(999L))
                .isInstanceOf(TabContentNotFoundException.class)
                .hasMessage("탭 내용을 찾을 수 없습니다.");
    }

    @Test
    void 탭_컨텐츠를_저장할_수_있다() {
        // given
        TabContent tabContent = TabContent.create(TabId.create(2L), "새로운 컨텐츠");

        // when
        TabContent actual = jdbcTabContentRepository.save(tabContent);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getTabId()).isEqualTo(2L),
                () -> assertThat(actual.getContent()).isEqualTo("새로운 컨텐츠")
        );
    }

    @Test
    void 저장_후_조회하면_동일한_내용을_반환한다() {
        // given
        TabContent tabContent = TabContent.create(TabId.create(2L), "저장 테스트");
        TabContent saved = jdbcTabContentRepository.save(tabContent);

        // when
        TabContent actual = jdbcTabContentRepository.findById(saved.getId());

        // then
        assertThat(actual.getContent()).isEqualTo("저장 테스트");
    }

    @Test
    void 탭_컨텐츠의_내용을_수정할_수_있다() {
        // given
        TabContent original = jdbcTabContentRepository.findById(1L);
        TabContent updated = original.updateContent("수정된 내용");

        // when
        jdbcTabContentRepository.update(updated);

        // then
        TabContent actual = jdbcTabContentRepository.findById(1L);
        assertThat(actual.getContent()).isEqualTo("수정된 내용");
    }

    @Test
    void 수정_후_ID는_유지된다() {
        // given
        TabContent original = jdbcTabContentRepository.findById(1L);
        TabContent updated = original.updateContent("ID 유지 테스트");

        // when
        jdbcTabContentRepository.update(updated);

        // then
        TabContent actual = jdbcTabContentRepository.findById(1L);
        assertThat(actual.getId()).isEqualTo(1L);
    }

    @Test
    void 탭_컨텐츠를_삭제할_수_있다() {
        // given
        TabContent tabContent = TabContent.create(TabId.create(2L), "삭제할 컨텐츠");
        TabContent saved = jdbcTabContentRepository.save(tabContent);
        Long contentId = saved.getId();

        // when
        jdbcTabContentRepository.delete(contentId);

        // then
        assertThatThrownBy(() -> jdbcTabContentRepository.findById(contentId))
                .isInstanceOf(TabContentNotFoundException.class)
                .hasMessage("탭 내용을 찾을 수 없습니다.");
    }

    @Test
    void 탭_ID로_모든_컨텐츠를_삭제할_수_있다() {
        // given
        TabId tabId = TabId.create(1L);
        int beforeCount = jdbcTabContentRepository.countByTabId(tabId);

        // when
        jdbcTabContentRepository.deleteAllByTabId(tabId);

        // then
        assertAll(
                () -> assertThat(beforeCount).isEqualTo(2),
                () -> assertThat(jdbcTabContentRepository.countByTabId(tabId)).isZero()
        );
    }

    @Test
    void 탭의_컨텐츠_개수를_조회할_수_있다() {
        // when
        int actual = jdbcTabContentRepository.countByTabId(TabId.create(1L));

        // then
        assertThat(actual).isEqualTo(2);
    }
}
