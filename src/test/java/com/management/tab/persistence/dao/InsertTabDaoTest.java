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
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/insert-tab-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class InsertTabDaoTest {

    @Autowired
    InsertTabDao insertTabDao;

    @Autowired
    SelectTabDao selectTabDao;

    private static final Long TEST_PARENT_ID = 100L;

    @Test
    void 루트_탭을_저장한다() {
        // given
        Long groupId = 1L;
        String title = "테스트 탭";
        String url = "http://test.com";
        int position = 0;
        LocalDateTime now = LocalDateTime.now();

        // when
        Long tabId = insertTabDao.saveRootTab(groupId, title, url, position, now, now);

        // then
        assertThat(selectTabDao.findById(tabId)).isPresent();
    }

    @Test
    void 루트_탭의_parent_id는_null이다() {
        // given
        Long groupId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // when
        Long tabId = insertTabDao.saveRootTab(groupId, "루트 탭", "http://test.com", 0, now, now);

        // then
        assertThat(selectTabDao.findParentId(tabId)).isEmpty();
    }

    @Test
    void 자식_탭을_저장한다() {
        // given
        Long groupId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // when
        Long childId = insertTabDao.saveChildTab(groupId, "자식", "http://child.com", TEST_PARENT_ID, 0, now, now);

        // then
        assertThat(selectTabDao.findById(childId)).isPresent();
    }

    @Test
    void 자식_탭의_parent_id가_설정된다() {
        // given
        Long groupId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // when
        Long childId = insertTabDao.saveChildTab(groupId, "자식", "http://child.com", TEST_PARENT_ID, 0, now, now);

        // then
        assertThat(selectTabDao.findParentId(childId)).contains(TEST_PARENT_ID);
    }

    @Test
    void 같은_부모의_여러_자식을_저장할_수_있다() {
        // given
        Long groupId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // when
        insertTabDao.saveChildTab(groupId, "자식1", "http://child1.com", TEST_PARENT_ID, 0, now, now);
        insertTabDao.saveChildTab(groupId, "자식2", "http://child2.com", TEST_PARENT_ID, 1, now, now);
        insertTabDao.saveChildTab(groupId, "자식3", "http://child3.com", TEST_PARENT_ID, 2, now, now);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(TEST_PARENT_ID);
        assertThat(actual).hasSize(3);
    }

    @Test
    void 다른_그룹에_같은_제목의_탭을_저장할_수_있다() {
        // given
        Long groupId1 = 1L;
        Long groupId2 = 2L;
        String sameTitle = "동일한 제목";
        LocalDateTime now = LocalDateTime.now();

        // when
        Long id1 = insertTabDao.saveRootTab(groupId1, sameTitle, "http://test.com", 0, now, now);
        Long id2 = insertTabDao.saveRootTab(groupId2, sameTitle, "http://test.com", 0, now, now);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(id1)).isPresent(),
                () -> assertThat(selectTabDao.findById(id2)).isPresent(),
                () -> assertThat(id1).isNotEqualTo(id2)
        );
    }

    @Test
    void 트리_구조로_탭을_조회할_수_있다() {
        // given
        Long groupId = 1L;
        LocalDateTime now = LocalDateTime.now();

        Long rootId = insertTabDao.saveRootTab(groupId, "루트", "http://root.com", 0, now, now);
        Long childId = insertTabDao.saveChildTab(groupId, "자식", "http://child.com", rootId, 0, now, now);
        insertTabDao.saveChildTab(groupId, "손자", "http://grandchild.com", childId, 0, now, now);

        // when
        List<TabWithDepthDto> actual = selectTabDao.findTreeByGroup(groupId);

        // then
        assertAll(
                () -> assertThat(actual).hasSize(4),
                () -> assertThat(actual.stream().anyMatch(t -> t.depth() == 0)).isTrue(),
                () -> assertThat(actual.stream().anyMatch(t -> t.depth() == 1)).isTrue(),
                () -> assertThat(actual.stream().anyMatch(t -> t.depth() == 2)).isTrue()
        );
    }

    @Test
    void position이_올바르게_저장된다() {
        // given
        Long groupId = 1L;
        LocalDateTime now = LocalDateTime.now();

        // when
        insertTabDao.saveRootTab(groupId, "첫번째", "http://1.com", 0, now, now);
        insertTabDao.saveRootTab(groupId, "두번째", "http://2.com", 1, now, now);
        insertTabDao.saveRootTab(groupId, "세번째", "http://3.com", 2, now, now);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(null);
        assertAll(
                () -> assertThat(actual).hasSize(4), // 기존 부모(100) + 새로 생성한 3개
                () -> assertThat(actual.stream().filter(t -> t.position() == 0).count()).isEqualTo(2),
                () -> assertThat(actual.stream().anyMatch(t -> t.position() == 1)).isTrue(),
                () -> assertThat(actual.stream().anyMatch(t -> t.position() == 2)).isTrue()
        );
    }
}
