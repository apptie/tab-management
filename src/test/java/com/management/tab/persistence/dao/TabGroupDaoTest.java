package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabGroupDto;
import java.time.LocalDateTime;
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
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/insert-tab-group-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class TabGroupDaoTest {

    @Autowired
    TabGroupDao tabGroupDao;

    @Test
    void 모든_탭_그룹을_조회할_수_있다() {
        // when
        List<TabGroupDto> actual = tabGroupDao.findAll();

        // then
        assertThat(actual).hasSize(3);
    }

    @Test
    void ID로_탭_그룹을_조회할_수_있다() {
        // given
        Long id = 1L;

        // when
        Optional<TabGroupDto> actual = tabGroupDao.findById(id);

        // then
        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get().id()).isEqualTo(1L),
                () -> assertThat(actual.get().name()).isEqualTo("개발 탭")
        );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_빈_Optional을_반환한다() {
        // given
        Long id = 999L;

        // when
        Optional<TabGroupDto> actual = tabGroupDao.findById(id);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 새로운_탭_그룹을_저장할_수_있다() {
        // given
        String name = "새 탭 그룹";

        // when
        Long actual = tabGroupDao.save(name, LocalDateTime.now(), LocalDateTime.now());

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual).isPositive()
        );
    }

    @Test
    void 탭_그룹의_이름을_수정할_수_있다() {
        // given
        Long id = 1L;
        String newName = "수정된 개발 탭";

        // when
        tabGroupDao.update(id, newName);

        // then
        Optional<TabGroupDto> actual = tabGroupDao.findById(id);
        assertAll(
                () -> assertThat(actual).isPresent(),
                () -> assertThat(actual.get().name()).isEqualTo(newName)
        );
    }

    @Test
    void 탭_그룹을_삭제할_수_있다() {
        // given
        Long id = 1L;

        // when
        tabGroupDao.delete(id);

        // then
        Optional<TabGroupDto> actual = tabGroupDao.findById(id);
        assertThat(actual).isEmpty();
    }

    @Test
    void 탭_그룹_삭제시_해당_그룹의_탭도_함께_삭제된다() {
        // given
        Long groupId = 1L;
        int beforeCount = tabGroupDao.countTabs(groupId);

        // when
        tabGroupDao.delete(groupId);

        // then
        int actual = tabGroupDao.countTabs(groupId);
        assertAll(
                () -> assertThat(beforeCount).isEqualTo(2),
                () -> assertThat(actual).isZero()
        );
    }

    @Test
    void 탭_그룹의_탭_개수를_조회할_수_있다() {
        // given
        Long groupId = 1L;

        // when
        int actual = tabGroupDao.countTabs(groupId);

        // then
        assertThat(actual).isEqualTo(2);
    }

    @Test
    void 탭이_없는_그룹의_탭_개수는_0이다() {
        // given
        Long groupId = 3L;

        // when
        int actual = tabGroupDao.countTabs(groupId);

        // then
        assertThat(actual).isZero();
    }

    @Test
    void 존재하지_않는_그룹의_탭_개수는_0이다() {
        // given
        Long groupId = 999L;

        // when
        int actual = tabGroupDao.countTabs(groupId);

        // then
        assertThat(actual).isZero();
    }

    @Test
    void 모든_탭_그룹은_ID_순서로_정렬되어_조회된다() {
        // when
        List<TabGroupDto> actual = tabGroupDao.findAll();

        // then
        assertAll(
                () -> assertThat(actual.get(0).id()).isEqualTo(1L),
                () -> assertThat(actual.get(1).id()).isEqualTo(2L),
                () -> assertThat(actual.get(2).id()).isEqualTo(3L)
        );
    }
}
