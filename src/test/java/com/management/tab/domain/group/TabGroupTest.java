package com.management.tab.domain.group;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabGroupTest {

    @Test
    void TabGroup을_초기화할_수_있다() {
        // when
        TabGroup actual = TabGroup.create(1L, "테스트 그룹");

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNull(),
                () -> assertThat(actual.getName()).isEqualTo("테스트 그룹"),
                () -> assertThat(actual.getCreatorId()).isEqualTo(1L),
                () -> assertThat(actual.getCreatedAt()).isNotNull(),
                () -> assertThat(actual.getUpdatedAt()).isNotNull()
        );
    }

    @Test
    void 지정한_시각과_ID로_TabGroup을_생성할_수_있다() {
        // given
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);

        // when
        TabGroup actual = TabGroup.create(100L, 1L, "테스트 그룹", createdAt, updatedAt);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(100L),
                () -> assertThat(actual.getName()).isEqualTo("테스트 그룹"),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(createdAt),
                () -> assertThat(actual.getUpdatedAt()).isEqualTo(updatedAt)
        );
    }

    @Test
    void id를_전달하지_않으면_초기화된_TabGroup은_id가_할당되지_않는다() {
        // when
        TabGroup actual = TabGroup.create(1L, "테스트 그룹");

        // then
        assertThat(actual.getId()).isNull();
    }

    @Test
    void 기존_TabGroup에_id를_할당할_수_있다() {
        // given
        TabGroup existingTabGroup = TabGroup.create(1L, "기존 그룹");

        // when
        TabGroup actual = existingTabGroup.updateAssignedId(100L);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(100L),
                () -> assertThat(actual.getName()).isEqualTo("기존 그룹")
        );
    }

    @Test
    void TabGroup의_이름을_변경할_수_있다() {
        // given
        TabGroup tabGroup = TabGroup.create(1L, "원래 이름");

        // when
        TabGroup actual = tabGroup.rename("변경된 이름");

        // then
        assertThat(actual.getName()).isEqualTo("변경된 이름");
    }

    @Test
    void 이름_변경_시_ID는_유지된다() {
        // given
        TabGroup tabGroup = TabGroup.create(
                100L,
                1L,
                "원래 이름",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        TabGroup actual = tabGroup.rename("변경된 이름");

        // then
        assertAll(
                () -> assertThat(actual.getId()).isEqualTo(100L),
                () -> assertThat(actual.getName()).isEqualTo("변경된 이름")
        );
    }

    @Test
    void 같은_ID를_가진_writerId로_비교하면_해당_탭_그룹의_작성자이다() {
        // given
        TabGroup tabGroup = TabGroup.create(
                100L,
                1L,
                "테스트 그룹",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        boolean result = tabGroup.isWriter(100L);

        // then
        assertThat(result).isTrue();
    }

    @Test
    void 다른_ID를_가진_writerId로_비교하면_해당_탭_그룹의_작성자가_아니다() {
        // given
        TabGroup tabGroup = TabGroup.create(
                100L,
                1L,
                "테스트 그룹",
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        // when
        boolean result = tabGroup.isNotWriter(200L);

        // then
        assertThat(result).isTrue();
    }
}
