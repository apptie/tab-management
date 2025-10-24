package com.management.tab.persistence.dao;

import com.management.tab.persistence.dao.dto.TabDto;
import com.management.tab.persistence.dao.dto.TabWithDepthDto;
import java.util.Comparator;
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
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/dao/select-tab-dao-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class SelectTabDaoTest {

    @Autowired
    SelectTabDao selectTabDao;

    @Test
    void ID로_탭을_조회한다() {
        // when
        Optional<TabDto> actual = selectTabDao.findById(100L);

        // then
        assertThat(actual)
                .isPresent()
                .hasValueSatisfying(
                        tab -> assertAll(
                                () -> assertThat(tab.id()).isEqualTo(100L),
                                () -> assertThat(tab.groupId()).isEqualTo(1L),
                                () -> assertThat(tab.title()).isEqualTo("조회테스트_루트1"),
                                () -> assertThat(tab.url()).isEqualTo("http://root1.com"),
                                () -> assertThat(tab.position()).isZero()
                        )
                );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_빈_Optional을_반환한다() {
        // when
        Optional<TabDto> actual = selectTabDao.findById(999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 그룹별로_트리를_조회한다() {
        // when
        List<TabWithDepthDto> actual = selectTabDao.findTreeByGroup(1L);

        // then
        assertThat(actual).hasSize(5);
    }

    @Test
    void 트리_조회_시_정확한_depth를_조회한다() {
        // when
        List<TabWithDepthDto> actual = selectTabDao.findTreeByGroup(1L);

        // then
        assertAll(
                () -> assertThat(actual)
                        .filteredOn(t -> t.id().equals(100L))
                        .first()
                        .satisfies(tab -> assertThat(tab.depth()).isZero()),
                () -> assertThat(actual)
                        .filteredOn(t -> t.id().equals(101L))
                        .first()
                        .satisfies(tab -> assertThat(tab.depth()).isEqualTo(1)),
                () -> assertThat(actual)
                        .filteredOn(t -> t.id().equals(103L))
                        .first()
                        .satisfies(tab -> assertThat(tab.depth()).isEqualTo(2))
        );
    }

    @Test
    void 트리_조회_시_depth와_position_순으로_정렬된다() {
        // when
        List<TabWithDepthDto> actual = selectTabDao.findTreeByGroup(1L);

        // then
        assertThat(actual).isSortedAccordingTo(
                Comparator.comparing(TabWithDepthDto::depth)
                          .thenComparing(TabWithDepthDto::position)
        );
    }

    @Test
    void 다른_그룹의_탭은_조회되지_않는다() {
        // when
        List<TabWithDepthDto> actual = selectTabDao.findTreeByGroup(1L);

        // then
        assertThat(actual).noneMatch(t -> t.id().equals(200L));
    }

    @Test
    void 부모_ID를_조회한다() {
        // when
        Optional<Long> actual = selectTabDao.findParentId(101L);

        // then
        assertThat(actual).hasValue(100L);
    }

    @Test
    void 루트_탭의_부모_ID를_조회하면_빈_Optional을_반환한다() {
        // when
        Optional<Long> actual = selectTabDao.findParentId(100L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 존재하지_않는_탭의_부모_ID를_조회하면_빈_Optional을_반환한다() {
        // when
        Optional<Long> actual = selectTabDao.findParentId(999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 형제_탭들을_조회한다() {
        // when
        List<TabDto> actual = selectTabDao.findSiblings(100L);

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void 형제_탭_조회_시_position_순으로_정렬된다() {
        // when
        List<TabDto> actual = selectTabDao.findSiblings(100L);

        // then
        assertAll(
                () -> assertThat(actual.get(0).id()).isEqualTo(101L),
                () -> assertThat(actual.get(1).id()).isEqualTo(102L)
        );
    }

    @Test
    void 부모가_null인_경우_빈_리스트를_반환한다() {
        // when
        List<TabDto> actual = selectTabDao.findSiblings(null);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 루트_레벨_형제들을_조회한다() {
        // when
        List<TabDto> actual = selectTabDao.findRootSiblings();

        // then
        assertThat(actual).filteredOn(t -> t.groupId().equals(1L))
                          .hasSizeGreaterThanOrEqualTo(1);
    }

    @Test
    void 자식이_없는_탭의_형제_조회_시_빈_리스트를_반환한다() {
        // when
        List<TabDto> actual = selectTabDao.findSiblings(999L);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 특정_부모의_마지막_position을_조회한다() {
        // when
        int actual = selectTabDao.findTabLastPosition(1L, 100L);

        // then
        assertThat(actual).isEqualTo(1);
    }

    @Test
    void 자식이_없는_탭의_마지막_position을_조회하면_0을_반환한다() {
        // when
        int actual = selectTabDao.findTabLastPosition(1L, 101L);

        // then
        assertThat(actual).isZero();
    }

    @Test
    void 루트_레벨의_마지막_position을_조회한다() {
        // when
        int actual = selectTabDao.findRootTabLastPosition(1L);

        // then
        assertThat(actual).isGreaterThanOrEqualTo(1);
    }

    @Test
    void 탭이_없는_그룹의_루트_position을_조회하면_0을_반환한다() {
        // when
        int actual = selectTabDao.findRootTabLastPosition(999L);

        // then
        assertThat(actual).isZero();
    }

    @Test
    void 존재하지_않는_부모의_마지막_position을_조회하면_0을_반환한다() {
        // when
        int actual = selectTabDao.findTabLastPosition(1L, 999L);

        // then
        assertThat(actual).isZero();
    }

    @Test
    void 그룹별로_자식_position이_별도로_관리된다() {
        // when
        int group1ChildPosition = selectTabDao.findTabLastPosition(1L, 100L);
        int group2ChildPosition = selectTabDao.findTabLastPosition(2L, 200L);

        // then
        assertAll(
                () -> assertThat(group1ChildPosition).isGreaterThanOrEqualTo(0),
                () -> assertThat(group2ChildPosition).isGreaterThanOrEqualTo(0)
        );
    }
}
