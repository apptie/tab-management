package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabDto;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/dao/update-tab-dao-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class UpdateTabDaoTest {

    @Autowired
    UpdateTabDao updateTabDao;

    @Autowired
    SelectTabDao selectTabDao;

    @Test
    void 자식이_없는_탭만_이동한다() {
        // when
        updateTabDao.updateMovingTabOnly(302L, 300L, 310L);

        // then
        Optional<Long> actual = selectTabDao.findParentId(302L);
        assertThat(actual).hasValue(310L);
    }

    @Test
    void 자식이_있는_탭을_이동하면_자식들은_원래_부모에게_올라간다() {
        // when
        updateTabDao.updateMovingTabOnly(301L, 300L, 310L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(301L)).hasValue(310L),
                () -> assertThat(selectTabDao.findParentId(303L)).hasValue(300L),
                () -> assertThat(selectTabDao.findParentId(304L)).hasValue(300L)
        );
    }

    @Test
    void 탭을_루트로_이동한다() {
        // when
        updateTabDao.updateMovingTabOnly(301L, 300L, null);

        // then
        assertThat(selectTabDao.findParentId(301L)).isEmpty();
    }

    @Test
    void 자식들은_형제_관계가_유지된다() {
        // when
        updateTabDao.updateMovingTabOnly(301L, 300L, 310L);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(300L);
        assertThat(actual).anyMatch(t -> t.id().equals(303L) || t.id().equals(304L));
    }

    @Test
    void 하위_트리와_함께_이동한다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(301L, 310L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(301L)).hasValue(310L),
                () -> assertThat(selectTabDao.findParentId(303L)).hasValue(301L),
                () -> assertThat(selectTabDao.findParentId(304L)).hasValue(301L)
        );
    }

    @Test
    void 하위_트리와_함께_루트로_이동한다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(301L, null);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(301L)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(303L)).hasValue(301L)
        );
    }

    @Test
    void 하위_트리_이동_시_트리_구조가_유지된다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(301L, 310L);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(301L);
        assertThat(actual).hasSize(2);
    }

    @Test
    void position을_업데이트한다() {
        // when
        updateTabDao.updatePosition(301L, 5);

        // then
        Optional<TabDto> actual = selectTabDao.findById(301L);
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(tab -> assertThat(tab.position()).isEqualTo(5));
    }

    @Test
    void 탭_정보를_업데이트한다() {
        // when
        updateTabDao.updateTab(301L, "변경된 제목", "http://new-url.com");

        // then
        Optional<TabDto> actual = selectTabDao.findById(301L);
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(tab -> assertAll(
                        () -> assertThat(tab.title()).isEqualTo("변경된 제목"),
                        () -> assertThat(tab.url()).isEqualTo("http://new-url.com")
                ));
    }

    @Test
    void 이동_후_다른_탭은_영향받지_않는다() {
        // when
        updateTabDao.updateMovingTabOnly(301L, 300L, 310L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(302L)).hasValue(300L),
                () -> assertThat(selectTabDao.findParentId(311L)).hasValue(310L)
        );
    }

    @Test
    void 하위_트리_이동_후_원래_위치의_탭들은_영향받지_않는다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(301L, 310L);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(302L)).hasValue(300L),
                () -> assertThat(selectTabDao.findById(302L)).isPresent()
        );
    }
}
