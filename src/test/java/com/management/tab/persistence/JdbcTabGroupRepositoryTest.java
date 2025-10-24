package com.management.tab.persistence;

import com.management.tab.domain.group.TabGroup;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Sql(scripts = {"classpath:sql/schema.sql", "classpath:sql/insert-jdbc-tab-group-repository-test-data.sql"})
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SuppressWarnings("NonAsciiCharacters")
class JdbcTabGroupRepositoryTest {

    @Autowired
    JdbcTabGroupRepository jdbcTabGroupRepository;

    @Test
    void 모든_탭_그룹을_조회할_수_있다() {
        // when
        List<TabGroup> actual = jdbcTabGroupRepository.findAll();

        // then
        assertThat(actual).isNotEmpty();
    }

    @Test
    void 조회한_탭_그룹_목록에_모든_그룹이_포함된다() {
        // when
        List<TabGroup> actual = jdbcTabGroupRepository.findAll();

        // then
        assertThat(actual).hasSizeGreaterThanOrEqualTo(2);
    }

    @Test
    void ID로_탭_그룹을_조회할_수_있다() {
        // when
        TabGroup actual = jdbcTabGroupRepository.findById(1L);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(1L),
                () -> assertThat(actual.getName()).isEqualTo("테스트 그룹1")
        );
    }

    @Test
    void 존재하지_않는_ID로_조회하면_예외가_발생한다() {
        // given
        Long groupId = 999L;

        // when & then
        assertThatThrownBy(() -> jdbcTabGroupRepository.findById(groupId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지정한 ID에 해당하는 탭 그룹이 없습니다.");
    }

    @Test
    void 탭_그룹을_저장할_수_있다() {
        // given
        TabGroup tabGroup = TabGroup.create("새 그룹");

        // when
        TabGroup actual = jdbcTabGroupRepository.save(tabGroup);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("새 그룹")
        );
    }

    @Test
    void 탭_그룹의_이름을_변경할_수_있다() {
        // given
        TabGroup tabGroup = jdbcTabGroupRepository.findById(1L);
        TabGroup renamedTabGroup = tabGroup.rename("변경된 그룹명");

        // when
        jdbcTabGroupRepository.updateRenamed(renamedTabGroup);

        // then
        TabGroup actual = jdbcTabGroupRepository.findById(1L);

        assertThat(actual.getName()).isEqualTo("변경된 그룹명");
    }

    @Test
    void 이름_변경_시_ID는_유지된다() {
        // given
        TabGroup tabGroup = jdbcTabGroupRepository.findById(1L);
        TabGroup renamedTabGroup = tabGroup.rename("변경된 그룹명");

        // when
        jdbcTabGroupRepository.updateRenamed(renamedTabGroup);

        // then
        TabGroup actual = jdbcTabGroupRepository.findById(1L);

        assertThat(actual.getId()).isEqualTo(1L);
    }

    @Test
    void 탭_그룹을_삭제할_수_있다() {
        // given
        TabGroup tabGroup = TabGroup.create("삭제할 그룹");
        TabGroup saved = jdbcTabGroupRepository.save(tabGroup);
        Long groupId = saved.getId();

        // when
        jdbcTabGroupRepository.delete(groupId);

        // then
        assertThatThrownBy(() -> jdbcTabGroupRepository.findById(groupId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("지정한 ID에 해당하는 탭 그룹이 없습니다.");
    }

    @Test
    void 탭이_있는_그룹의_탭_개수를_조회한다() {
        // given
        Long groupId = 1L;

        // when
        int actual = jdbcTabGroupRepository.countTabs(groupId);

        // then
        assertThat(actual).isEqualTo(1);
    }
}
