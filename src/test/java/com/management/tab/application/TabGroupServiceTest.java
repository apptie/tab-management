package com.management.tab.application;

import com.management.tab.domain.group.TabGroup;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/service/tab-group-service-test-data.sql"})
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabGroupServiceTest {

    @Autowired
    TabGroupService tabGroupService;

    @Test
    void 모든_탭_그룹을_조회한다() {
        // when
        List<TabGroup> actual = tabGroupService.getAllGroups();

        // then
        assertThat(actual).hasSize(2);
    }

    @Test
    void ID로_특정_탭_그룹을_조회한다() {
        // when
        TabGroup actual = tabGroupService.getGroup(1L);

        // then
        assertThat(actual.getName()).isEqualTo("테스트 그룹1");
    }

    @Test
    void 새로운_탭_그룹을_초기화한다() {
        // when
        Long actual = tabGroupService.createGroup("새로운 그룹");

        // then
        assertThat(actual).isNotNull();
    }

    @Test
    void 탭_그룹_이름을_변경한다() {
        // when
        tabGroupService.updateGroup(1L, "변경된 이름");

        // then
        TabGroup actual = tabGroupService.getGroup(1L);

        assertThat(actual.getName()).isEqualTo("변경된 이름");
    }

    @Test
    void 탭_그룹을_삭제한다() {
        // when
        tabGroupService.delete(2L);

        // then
        List<TabGroup> actual = tabGroupService.getAllGroups();

        assertAll(
                () -> assertThat(actual).hasSize(1),
                () -> assertThat(actual)
                        .extracting(TabGroup::getId)
                        .doesNotContain(2L)
        );
    }

    @Test
    void 탭_그룹이_가지고_있는_탭_개수를_조회한다() {
        // when
        int actual = tabGroupService.countTabs(1L);

        // then
        assertThat(actual).isEqualTo(6);
    }

    @ParameterizedTest(name = "{0}일 때 탭 그룹을 초기화할 수 없다")
    @NullAndEmptySource
    void 빈_이름으로_탭_그룹을_초기화할_수_없다(String name) {
        // when & then
        assertThatThrownBy(() -> tabGroupService.createGroup(name))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("탭 그룹 이름은 비어있을 수 없습니다.");
    }
}

