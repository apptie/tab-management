package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabDto;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/delete-tab-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class DeleteTabDaoTest {

    private static final Long ROOT_ID = 200L;
    private static final Long CHILD1_ID = 201L;
    private static final Long CHILD2_ID = 202L;
    private static final Long GRANDCHILD1_ID = 203L;
    private static final Long GRANDCHILD2_ID = 204L;
    private static final Long GRANDCHILD3_ID = 205L;

    @Autowired
    DeleteTabDao deleteTabDao;

    @Autowired
    SelectTabDao selectTabDao;

    @Test
    void 리프_노드를_삭제한다() {
        // when
        deleteTabDao.deleteTabOnly(GRANDCHILD1_ID, CHILD1_ID);

        // then
        assertThat(selectTabDao.findById(GRANDCHILD1_ID)).isEmpty();
    }

    @Test
    void 자식이_있는_탭을_삭제하면_자식들이_부모에게_올라간다() {
        // when
        deleteTabDao.deleteTabOnly(CHILD1_ID, ROOT_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(CHILD1_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(GRANDCHILD1_ID)).contains(ROOT_ID),
                () -> assertThat(selectTabDao.findParentId(GRANDCHILD2_ID)).contains(ROOT_ID)
        );
    }

    @Test
    void 루트_탭을_삭제하면_자식들이_루트가_된다() {
        // when
        deleteTabDao.deleteTabOnly(ROOT_ID, null);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(ROOT_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(CHILD1_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(CHILD2_ID)).isEmpty()
        );
    }

    @Test
    void 형제_관계가_유지된다() {
        // when
        deleteTabDao.deleteTabOnly(CHILD1_ID, ROOT_ID);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(ROOT_ID);
        assertThat(actual.stream()
                         .anyMatch(t -> t.id().equals(GRANDCHILD1_ID)))
                .isTrue();
    }

    @Test
    void 탭과_하위_트리를_모두_삭제한다() {
        // when
        deleteTabDao.deleteTabWithSubtree(CHILD1_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(CHILD1_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(GRANDCHILD1_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(GRANDCHILD2_ID)).isEmpty()
        );
    }

    @Test
    void 하위_트리_삭제_시_다른_탭은_영향받지_않는다() {
        // when
        deleteTabDao.deleteTabWithSubtree(CHILD1_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(ROOT_ID)).isPresent(),
                () -> assertThat(selectTabDao.findById(CHILD2_ID)).isPresent(),
                () -> assertThat(selectTabDao.findById(GRANDCHILD3_ID)).isPresent()
        );
    }

    @Test
    void 여러_자식을_가진_탭을_삭제한다() {
        // when
        deleteTabDao.deleteTabOnly(CHILD1_ID, ROOT_ID);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(ROOT_ID);
        assertThat(actual.stream()
                         .filter(t -> t.id().equals(GRANDCHILD1_ID) || t.id().equals(GRANDCHILD2_ID))
                         .count())
                .isEqualTo(2);
    }

    @Test
    void 전체_트리를_삭제한다() {
        // when
        deleteTabDao.deleteTabWithSubtree(ROOT_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(ROOT_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(CHILD1_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(CHILD2_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(GRANDCHILD1_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(GRANDCHILD2_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(GRANDCHILD3_ID)).isEmpty()
        );
    }

    @Test
    void 일부_하위_트리만_삭제한다() {
        // when
        deleteTabDao.deleteTabWithSubtree(CHILD2_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(CHILD2_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(GRANDCHILD3_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findById(ROOT_ID)).isPresent(),
                () -> assertThat(selectTabDao.findById(CHILD1_ID)).isPresent()
        );
    }
}
