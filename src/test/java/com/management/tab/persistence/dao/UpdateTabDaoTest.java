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
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/update-tab-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class UpdateTabDaoTest {

    @Autowired
    UpdateTabDao updateTabDao;

    @Autowired
    SelectTabDao selectTabDao;

    private static final Long ROOT_A_ID = 300L;
    private static final Long CHILD_A1_ID = 301L;
    private static final Long CHILD_A2_ID = 302L;
    private static final Long GRANDCHILD_A11_ID = 303L;
    private static final Long GRANDCHILD_A12_ID = 304L;
    private static final Long ROOT_B_ID = 310L;
    private static final Long CHILD_B1_ID = 311L;

    @Test
    void 자식이_없는_탭만_이동한다() {
        // when
        updateTabDao.updateMovingTabOnly(CHILD_A2_ID, ROOT_A_ID, ROOT_B_ID);

        // then
        Optional<Long> actual = selectTabDao.findParentId(CHILD_A2_ID);
        assertThat(actual).hasValue(ROOT_B_ID);
    }

    @Test
    void 자식이_있는_탭을_이동하면_자식들은_원래_부모에게_올라간다() {
        // when
        updateTabDao.updateMovingTabOnly(CHILD_A1_ID, ROOT_A_ID, ROOT_B_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(CHILD_A1_ID)).hasValue(ROOT_B_ID),
                () -> assertThat(selectTabDao.findParentId(GRANDCHILD_A11_ID)).hasValue(ROOT_A_ID),
                () -> assertThat(selectTabDao.findParentId(GRANDCHILD_A12_ID)).hasValue(ROOT_A_ID)
        );
    }

    @Test
    void 탭을_루트로_이동한다() {
        // when
        updateTabDao.updateMovingTabOnly(CHILD_A1_ID, ROOT_A_ID, null);

        // then
        assertThat(selectTabDao.findParentId(CHILD_A1_ID)).isEmpty();
    }

    @Test
    void 자식들은_형제_관계가_유지된다() {
        // when
        updateTabDao.updateMovingTabOnly(CHILD_A1_ID, ROOT_A_ID, ROOT_B_ID);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(ROOT_A_ID);
        assertThat(actual.stream()
                         .anyMatch(t -> t.id().equals(GRANDCHILD_A11_ID) || t.id().equals(GRANDCHILD_A12_ID)))
                .isTrue();
    }

    @Test
    void 하위_트리와_함께_이동한다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(CHILD_A1_ID, ROOT_B_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(CHILD_A1_ID)).hasValue(ROOT_B_ID),
                () -> assertThat(selectTabDao.findParentId(GRANDCHILD_A11_ID)).hasValue(CHILD_A1_ID),
                () -> assertThat(selectTabDao.findParentId(GRANDCHILD_A12_ID)).hasValue(CHILD_A1_ID)
        );
    }

    @Test
    void 하위_트리와_함께_루트로_이동한다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(CHILD_A1_ID, null);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(CHILD_A1_ID)).isEmpty(),
                () -> assertThat(selectTabDao.findParentId(GRANDCHILD_A11_ID)).hasValue(CHILD_A1_ID)
        );
    }

    @Test
    void 하위_트리_이동_시_트리_구조가_유지된다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(CHILD_A1_ID, ROOT_B_ID);

        // then
        List<TabDto> actual = selectTabDao.findSiblings(CHILD_A1_ID);
        assertThat(actual).hasSize(2);
    }

    @Test
    void position을_업데이트한다() {
        // when
        updateTabDao.updatePosition(CHILD_A1_ID, 5);

        // then
        Optional<TabDto> actual = selectTabDao.findById(CHILD_A1_ID);
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(tab -> assertThat(tab.position()).isEqualTo(5));
    }

    @Test
    void 탭_정보를_업데이트한다() {
        // given
        String newTitle = "변경된 제목";
        String newUrl = "http://new-url.com";

        // when
        updateTabDao.updateTab(CHILD_A1_ID, newTitle, newUrl);

        // then
        Optional<TabDto> actual = selectTabDao.findById(CHILD_A1_ID);
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(tab -> assertAll(
                        () -> assertThat(tab.title()).isEqualTo(newTitle),
                        () -> assertThat(tab.url()).isEqualTo(newUrl)
                ));
    }

    @Test
    void 이동_후_다른_탭은_영향받지_않는다() {
        // when
        updateTabDao.updateMovingTabOnly(CHILD_A1_ID, ROOT_A_ID, ROOT_B_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(CHILD_A2_ID)).hasValue(ROOT_A_ID),
                () -> assertThat(selectTabDao.findParentId(CHILD_B1_ID)).hasValue(ROOT_B_ID)
        );
    }

    @Test
    void 하위_트리_이동_후_원래_위치의_탭들은_영향받지_않는다() {
        // when
        updateTabDao.updateMovingTabWithSubtree(CHILD_A1_ID, ROOT_B_ID);

        // then
        assertAll(
                () -> assertThat(selectTabDao.findParentId(CHILD_A2_ID)).hasValue(ROOT_A_ID),
                () -> assertThat(selectTabDao.findById(CHILD_A2_ID)).isPresent()
        );
    }
}
