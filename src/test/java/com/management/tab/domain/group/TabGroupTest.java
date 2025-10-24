package com.management.tab.domain.group;

import org.junit.jupiter.api.Disabled;
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
    @Disabled("TabGroupId null object 패턴 적용 예정")
    void TabGroup을_초기화할_수_있다() {
        // given
        String name = "테스트 그룹";

        // when
        TabGroup actual = TabGroup.create(name);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNull(),
                () -> assertThat(actual.getName()).isEqualTo(name),
                () -> assertThat(actual.getCreatedAt()).isNotNull(),
                () -> assertThat(actual.getUpdatedAt()).isNotNull()
        );
    }

    @Test
    void 지정한_시각과_ID로_TabGroup을_생성할_수_있다() {
        // given
        Long groupId = 100L;
        String name = "테스트 그룹";
        LocalDateTime createdAt = LocalDateTime.of(2024, 1, 1, 10, 0);
        LocalDateTime updatedAt = LocalDateTime.of(2024, 1, 2, 15, 30);

        // when
        TabGroup actual = TabGroup.create(groupId, name, createdAt, updatedAt);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(groupId),
                () -> assertThat(actual.getName()).isEqualTo(name),
                () -> assertThat(actual.getCreatedAt()).isEqualTo(createdAt),
                () -> assertThat(actual.getUpdatedAt()).isEqualTo(updatedAt)
        );
    }

    @Test
    @Disabled("TabGroupId null object 패턴 적용 예정")
    void id를_전달하지_않으면_초기화된_TabGroup은_id가_할당되지_않는다() {
        // given
        String name = "테스트 그룹";

        // when
        TabGroup actual = TabGroup.create(name);

        // then
        assertThat(actual.getId()).isNull();
    }

    @Test
    void 기존_TabGroup에_id를_할당할_수_있다() {
        // given
        TabGroup existingTabGroup = TabGroup.create("기존 그룹");

        // when
        TabGroup actual = TabGroup.createWithAssignedId(100L, existingTabGroup);

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getId()).isEqualTo(100L),
                () -> assertThat(actual.getName()).isEqualTo("기존 그룹")
        );
    }

    @Test
    void TabGroup의_이름을_변경할_수_있다() {
        // given
        TabGroup tabGroup = TabGroup.create("원래 이름");

        // when
        TabGroup actual = tabGroup.rename("변경된 이름");

        // then
        assertAll(
                () -> assertThat(actual).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo("변경된 이름")
        );
    }

    @Test
    void 이름_변경_시_ID는_유지된다() {
        // given
        TabGroup tabGroup = TabGroup.create(
                100L,
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
}
