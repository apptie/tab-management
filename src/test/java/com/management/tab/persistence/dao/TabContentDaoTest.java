package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabContentDto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/insert-tab-content-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TabContentDaoTest {

    @Autowired
    TabContentDao tabContentDao;

    @Test
    void ID로_탭_컨텐츠를_조회할_수_있다() {
        // when
        Optional<TabContentDto> actual = tabContentDao.findById(1L);

        // then
        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get().id()).isEqualTo(1L),
                () -> assertThat(actual.get().tabId()).isEqualTo(1L),
                () -> assertThat(actual.get().content()).isEqualTo("Spring 프레임워크 학습 내용")
        );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_빈_Optional을_반환한다() {
        // when
        Optional<TabContentDto> actual = tabContentDao.findById(999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 탭_ID로_해당_탭의_모든_탭_컨텐츠를_조회할_수_있다() {
        // when
        List<TabContentDto> actual = tabContentDao.findAllByTabId(1L);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(2),
                () -> assertThat(actual.get(0).tabId()).isEqualTo(1L),
                () -> assertThat(actual.get(1).tabId()).isEqualTo(1L)
        );
    }

    @Test
    void 새로운_탭_컨텐츠를_저장할_수_있다() {
        // given
        LocalDateTime now = LocalDateTime.now();

        // when
        Long actual = tabContentDao.save(2L, "새로운 컨텐츠", now, now);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).isPositive()
        );
    }

    @Test
    void 탭_컨텐츠의_내용을_수정할_수_있다() {
        // given
        LocalDateTime updatedAt = LocalDateTime.now();

        // when
        tabContentDao.update(1L, "수정된 Spring 학습 내용", updatedAt);

        // then
        Optional<TabContentDto> actual = tabContentDao.findById(1L);
        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get().content()).isEqualTo("수정된 Spring 학습 내용")
        );
    }

    @Test
    void 탭_컨텐츠를_삭제할_수_있다() {
        // when
        tabContentDao.delete(1L);

        // then
        Optional<TabContentDto> actual = tabContentDao.findById(1L);
        assertThat(actual).isEmpty();
    }

    @Test
    void 탭_ID로_해당_탭의_모든_탭_컨텐츠를_삭제할_수_있다() {
        // given
        int beforeCount = tabContentDao.countByTabId(1L);

        // when
        tabContentDao.deleteAllByTabId(1L);

        // then
        assertAll(
                () -> assertThat(beforeCount).isEqualTo(2),
                () -> assertThat(tabContentDao.countByTabId(1L)).isZero()
        );
    }

    @Test
    void 탭_ID로_해당_탭의_모든_탭_컨텐츠_개수를_조회할_수_있다() {
        // when
        int actual = tabContentDao.countByTabId(1L);

        // then
        assertThat(actual).isEqualTo(2);
    }

    @Test
    void 특정_탭의_컨텐츠는_ID_순서로_정렬되어_조회된다() {
        // when
        List<TabContentDto> actual = tabContentDao.findAllByTabId(1L);

        // then
        assertAll(
                () -> assertThat(actual.get(0).id()).isEqualTo(1L),
                () -> assertThat(actual.get(1).id()).isEqualTo(2L)
        );
    }
}
