package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabDto;
import com.management.tab.persistence.dao.dto.TabWithDepthDto;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/dao/insert-tab-dao-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class InsertTabDaoTest {

    @Autowired
    InsertTabDao insertTabDao;

    @Autowired
    SelectTabDao selectTabDao;

    @Test
    void 루트_탭을_저장한다() {
        // when
        Long tabId = insertTabDao.saveRootTab(
                1L,
                "테스트 탭",
                "http://test.com",
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // then
        assertThat(selectTabDao.findById(tabId)).isPresent();
    }

    @Test
    void 루트_탭의_parent_id는_null이다() {
        // when
        Long tabId = insertTabDao.saveRootTab(
                1L,
                "루트 탭",
                "http://test.com",
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // then
        assertThat(selectTabDao.findParentId(tabId)).isEmpty();
    }

    @Test
    void 자식_탭을_저장한다() {
        // when
        Long childId = insertTabDao.saveChildTab(
                1L,
                "자식",
                "http://child.com",
                100L,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // then
        assertThat(selectTabDao.findById(childId)).isPresent();
    }

    @Test
    void 자식_탭의_parent_id가_설정된다() {
        // when
        Long childId = insertTabDao.saveChildTab(
                1L,
                "자식",
                "http://child.com",
                100L,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // then
        assertThat(selectTabDao.findParentId(childId)).contains(100L);
    }

    @Test
    void 같은_부모의_여러_자식을_저장할_수_있다() {
        // when
        insertTabDao.saveChildTab(
                1L,
                "자식1",
                "http://child1.com",
                100L,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        insertTabDao.saveChildTab(
                1L,
                "자식2",
                "http://child2.com",
                100L,
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        insertTabDao.saveChildTab(
                1L,
                "자식3",
                "http://child3.com",
                100L,
                2,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // then
        List<TabDto> actual = selectTabDao.findSiblings(100L);
        assertThat(actual).hasSize(3);
    }

    @Test
    void 다른_그룹에_같은_제목의_탭을_저장할_수_있다() {
        // when
        Long id1 = insertTabDao.saveRootTab(
                1L,
                "동일한 제목",
                "http://test.com",
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Long id2 = insertTabDao.saveRootTab(
                2L,
                "동일한 제목",
                "http://test.com",
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(id1)).isPresent(),
                () -> assertThat(selectTabDao.findById(id2)).isPresent(),
                () -> assertThat(id1).isNotEqualTo(id2)
        );
    }

    @Test
    void 트리_구조로_탭을_조회할_수_있다() {
        // when
        Long rootId = insertTabDao.saveRootTab(
                1L,
                "루트",
                "http://root.com",
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Long childId = insertTabDao.saveChildTab(
                1L,
                "자식",
                "http://child.com",
                rootId,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        insertTabDao.saveChildTab(
                1L,
                "손자",
                "http://grandchild.com",
                childId,
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        List<TabWithDepthDto> actual = selectTabDao.findTreeByGroup(1L);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(4),
                () -> assertThat(actual).anyMatch(t -> t.depth() == 0),
                () -> assertThat(actual).anyMatch(t -> t.depth() == 1),
                () -> assertThat(actual).anyMatch(t -> t.depth() == 2)
        );
    }

    @Test
    void position이_올바르게_저장된다() {
        // when
        insertTabDao.saveRootTab(
                1L,
                "첫번째",
                "http://1.com",
                0,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        insertTabDao.saveRootTab(
                1L,
                "두번째",
                "http://2.com",
                1,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        insertTabDao.saveRootTab(
                1L,
                "세번째",
                "http://3.com",
                2,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // then
        List<TabDto> actual = selectTabDao.findRootSiblings();
        assertAll(
                () -> assertThat(actual).hasSize(4),
                () -> assertThat(actual).filteredOn(t -> t.position() == 0).hasSize(2),
                () -> assertThat(actual).anyMatch(t -> t.position() == 1),
                () -> assertThat(actual).anyMatch(t -> t.position() == 2)
        );
    }
}
