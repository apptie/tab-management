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
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/dao/delete-tab-dao-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class DeleteTabDaoTest {

    @Autowired
    DeleteTabDao deleteTabDao;

    @Autowired
    SelectTabDao selectTabDao;

    @Test
    void 리프_노드를_삭제한다() {
        // when
        deleteTabDao.deleteTabOnly(203L, 201L);

        // then
        assertThat(selectTabDao.findById(203L)).isEmpty();
    }

    @Test
    void 자식이_있는_탭을_삭제하면_자식들이_부모에게_올라간다() {
        // when
        deleteTabDao.deleteTabOnly(201L, 200L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(201L)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(203L)).contains(200L),
                () -> assertThat(selectTabDao.findParentId(204L)).contains(200L)
        );
    }

    @Test
    void 루트_탭을_삭제하면_자식들이_루트가_된다() {
        // when
        deleteTabDao.deleteTabOnly(200L, null);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(200L)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(201L)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(202L)).isEmpty()
        );
    }

    @Test
    void 형제_관계가_유지된다() {
        // when
        deleteTabDao.deleteTabOnly(201L, 200L);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(200L);
        assertThat(actual.stream()
                         .anyMatch(t -> t.id().equals(203L)))
                .isTrue();
    }

    @Test
    void 탭과_하위_트리를_모두_삭제한다() {
        // when
        deleteTabDao.deleteTabWithSubtree(201L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(201L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(203L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(204L)).isEmpty()
        );
    }

    @Test
    void 하위_트리_삭제_시_다른_탭은_영향받지_않는다() {
        // when
        deleteTabDao.deleteTabWithSubtree(201L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(200L)).isPresent(),
                () -> assertThat(selectTabDao.findById(202L)).isPresent(),
                () -> assertThat(selectTabDao.findById(205L)).isPresent()
        );
    }

    @Test
    void 여러_자식을_가진_탭을_삭제한다() {
        // when
        deleteTabDao.deleteTabOnly(201L, 200L);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(200L);
        assertThat(actual.stream()
                         .filter(t -> t.id().equals(203L) || t.id().equals(204L))
                         .count())
                .isEqualTo(2);
    }

    @Test
    void 전체_트리를_삭제한다() {
        // when
        deleteTabDao.deleteTabWithSubtree(200L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(200L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(201L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(202L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(203L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(204L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(205L)).isEmpty()
        );
    }

    @Test
    void 일부_하위_트리만_삭제한다() {
        // when
        deleteTabDao.deleteTabWithSubtree(202L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findById(202L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(205L)).isEmpty(),
                () -> assertThat(selectTabDao.findById(200L)).isPresent(),
                () -> assertThat(selectTabDao.findById(201L)).isPresent()
        );
    }
}
